package ru.overcode.scrapper.service.linkrule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.scrapper.mapper.linkrule.TelegramChatLinkRuleMapper;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkRuleService {

    private final TelegramChatLinkRuleDbService telegramChatLinkRuleDbService;
    private final TelegramChatLinkRuleMapper telegramChatLinkRuleMapper;

    @Transactional
    public void upsertLinkRules(List<LinkRuleOutboxDto> linkRuleOutboxDtos) {
        Map<Long, LinkRuleOutboxDto> dtoById = linkRuleOutboxDtos.stream()
                .collect(Collectors.toMap(
                        LinkRuleOutboxDto::id,
                        Function.identity()
                ));

        Map<Long, TelegramChatLinkRule> existingEntities = telegramChatLinkRuleDbService.findAllById(dtoById.keySet());

        List<TelegramChatLinkRule> telegramChatLinkRules = dtoById.keySet().stream()
                .map(id -> existingEntities.getOrDefault(id, new TelegramChatLinkRule()))
                .peek(bindingRule -> telegramChatLinkRuleMapper
                        .fillTelegramChatLinkRule(bindingRule, dtoById.get(bindingRule.getId())))
                .toList();

        telegramChatLinkRuleDbService.saveAll(telegramChatLinkRules);
    }

    @Transactional
    public void deleteLinkRules(List<LinkRuleOutboxDto> linkRuleOutboxDtos) {
        Set<Long> ids = linkRuleOutboxDtos.stream()
                .map(LinkRuleOutboxDto::id)
                .collect(Collectors.toSet());

        telegramChatLinkRuleDbService.deleteAllByIdInBatch(ids);
    }
}
