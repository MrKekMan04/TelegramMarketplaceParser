package ru.overcode.gateway.service.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.dto.rule.GetRulesResponse;
import ru.overcode.gateway.dto.rule.RuleDto;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.mapper.chatlink.rule.TelegramChatLinkRuleMapper;
import ru.overcode.gateway.mapper.rule.RuleMapper;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRuleOutbox;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.gateway.service.chatlink.rule.TelegramChatLinkRuleDbService;
import ru.overcode.gateway.service.chatlink.rule.TelegramChatLinkRuleOutboxDbService;
import ru.overcode.gateway.service.link.LinkService;
import ru.overcode.gateway.service.marketrule.MarketRuleDbService;
import ru.overcode.gateway.service.rule.param.RuleParamsValidator;
import ru.overcode.gateway.service.telegramchat.TelegramChatService;
import ru.overcode.shared.dto.event.OutboxEventType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleDbService ruleDbService;
    private final TelegramChatLinkRuleDbService bindingRuleDbService;
    private final TelegramChatLinkRuleOutboxDbService outboxRuleDbService;
    private final TelegramChatService telegramChatService;
    private final LinkService linkService;
    private final MarketRuleDbService marketRuleDbService;
    @Qualifier("ruleParamsValidatorsByRuleId")
    private final Map<Long, RuleParamsValidator> ruleParamsValidators;
    private final TelegramChatLinkRuleMapper bindingRuleMapper;
    private final RuleMapper ruleMapper;

    @Transactional(readOnly = true)
    public List<GetRulesResponse> getRules(Long linkId) {
        linkService.getLinkOrThrow(linkId);

        List<Rule> rulesByLinkId = ruleDbService.findAllByLinkId(linkId).stream()
                .filter(rule -> ruleParamsValidators.containsKey(rule.getId()))
                .toList();

        return ruleMapper.toGetRulesResponse(rulesByLinkId, rulesByLinkId.stream()
                .collect(Collectors.toMap(
                        Rule::getId,
                        rule -> ruleParamsValidators.get(rule.getId()).getParamNames()
                )));
    }

    @Transactional
    public RuleDto addRule(Long chatId, Long linkId, Long ruleId, Map<String, String> ruleParams) {
        telegramChatService.throwIfNotExists(chatId);
        Link link = linkService.getLinkOrThrow(linkId);
        TelegramChatLink binding = linkService.getBindingOrThrow(chatId, linkId);

        if (!ruleParamsValidators.containsKey(ruleId)) {
            throw getNotFoundException(ruleId);
        }

        Rule rule = ruleDbService.findById(ruleId)
                .orElseThrow(() -> getNotFoundException(ruleId));

        marketRuleDbService.findByMarketIdAndRuleId(link.getMarketId(), ruleId)
                .orElseThrow(() -> getNotFoundException(ruleId));

        RuleParamsValidator paramsValidator = ruleParamsValidators.get(ruleId);
        if (!paramsValidator.validate(ruleParams)) {
            throw new UnprocessableEntityException(GatewayExceptionMessage.RULE_BAD_PARAMS
                    .withParam("ruleId", ruleId.toString())
                    .withParam("expectedParams", paramsValidator.getParamNames().toString())
                    .withParam("actualParams", ruleParams.keySet().toString()));
        }

        bindingRuleDbService.findByBindingIdAndRuleId(binding.getId(), ruleId)
                .ifPresent(ignore -> {
                    throw new UnprocessableEntityException(GatewayExceptionMessage.RULE_ALREADY_ADDED
                            .withParam("ruleId", ruleId.toString()));
                });

        TelegramChatLinkRule bindingRule = saveAndReplicateBindingRule(
                binding,
                ruleId,
                paramsValidator.getParamNames(),
                ruleParams
        );

        return new RuleDto(ruleId, paramsValidator.getRuleName(rule.getName(), bindingRule.getParams()));
    }

    @Transactional
    public void removeRule(Long chatId, Long linkId, Long ruleId) {
        telegramChatService.throwIfNotExists(chatId);
        linkService.getLinkOrThrow(linkId);
        TelegramChatLink binding = linkService.getBindingOrThrow(chatId, linkId);

        if (!ruleParamsValidators.containsKey(ruleId)) {
            throw getNotFoundException(ruleId);
        }

        ruleDbService.findById(ruleId)
                .orElseThrow(() -> getNotFoundException(ruleId));

        TelegramChatLinkRule bindingRule = bindingRuleDbService.findByBindingIdAndRuleId(binding.getId(), ruleId)
                .orElseThrow(() -> new UnprocessableEntityException(GatewayExceptionMessage.RULE_NOT_ADDED
                        .withParam("ruleId", ruleId.toString())));

        removeBindingRule(bindingRule.getId());
    }

    private UnprocessableEntityException getNotFoundException(Long ruleId) {
        return new UnprocessableEntityException(GatewayExceptionMessage.RULE_NOT_FOUND
                .withParam("ruleId", ruleId.toString()));
    }

    private TelegramChatLinkRule saveAndReplicateBindingRule(
            TelegramChatLink binding,
            Long ruleId,
            Set<String> validParamNames,
            Map<String, String> ruleParams
    ) {
        TelegramChatLinkRule bindingRule = bindingRuleDbService.save(bindingRuleMapper.toBindingRule(
                binding.getId(),
                ruleId,
                ruleMapper.fetchValidParams(validParamNames, ruleParams)
        ));
        outboxRuleDbService.save(bindingRuleMapper
                .toOutbox(bindingRule, binding.getLinkId(), ruleId, OutboxEventType.UPSERT));
        return bindingRule;
    }

    private void removeBindingRule(Long id) {
        bindingRuleDbService.deleteById(id);
        outboxRuleDbService.save(new TelegramChatLinkRuleOutbox()
                .setTelegramChatLinkRuleId(id)
                .setEventType(OutboxEventType.REMOVE));
    }
}
