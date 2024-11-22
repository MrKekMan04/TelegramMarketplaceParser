package ru.overcode.gateway.service.lnikupdate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.gateway.producer.linkupdate.LinkUpdateProducer;
import ru.overcode.gateway.service.chatlink.TelegramChatLinkDbService;
import ru.overcode.gateway.service.chatlink.rule.TelegramChatLinkRuleDbService;
import ru.overcode.gateway.service.link.LinkDbService;
import ru.overcode.gateway.service.rule.RuleDbService;
import ru.overcode.gateway.service.rule.param.RuleParamsValidator;
import ru.overcode.shared.stream.update.GatewayLinkUpdateDto;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdateProcessor {

    private final LinkUpdateProducer linkUpdateProducer;
    private final TelegramChatLinkRuleDbService bindingRuleDbService;
    private final TelegramChatLinkDbService bindingDbService;
    private final RuleDbService ruleDbService;
    private final LinkDbService linkDbService;

    @Qualifier("ruleParamsValidatorsByRuleId")
    private final Map<Long, RuleParamsValidator> ruleParamsValidators;

    @Transactional(readOnly = true)
    public void process(ScrapperLinkUpdateDto record) {
        TelegramChatLinkRule bindingRule = getBindingRule(record.id());
        if (bindingRule == null) {
            log.error("TelegramChatLinkRule with id {} not found", record.id());
            return;
        }
        TelegramChatLink binding = getBinding(bindingRule.getChatLinkId());
        if (binding == null) {
            log.error("TelegramChatLink with id {} not found", record.id());
            return;
        }
        Rule rule = getRule(bindingRule.getRuleId());
        if (rule == null) {
            log.error("Rule with id {} not found", bindingRule.getRuleId());
            return;
        }
        Link link = getLink(binding.getLinkId());
        if (link == null) {
            log.error("Link with id {} not found", binding.getLinkId());
            return;
        }

        linkUpdateProducer.send(new GatewayLinkUpdateDto(
                binding.getChatId(),
                binding.getLinkId(),
                link.getUrl(),
                ruleParamsValidators.get(rule.getId()).getRuleName(rule.getName(), bindingRule.getParams())
        ));
    }

    private TelegramChatLinkRule getBindingRule(Long id) {
        return bindingRuleDbService.findById(id)
                .orElse(null);
    }

    private TelegramChatLink getBinding(Long id) {
        return bindingDbService.findById(id)
                .orElse(null);
    }

    private Rule getRule(Long id) {
        return ruleDbService.findById(id)
                .orElse(null);
    }

    private Link getLink(Long id) {
        return linkDbService.findById(id)
                .orElse(null);
    }
}
