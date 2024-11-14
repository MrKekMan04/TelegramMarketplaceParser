package ru.overcode.scrapper.service.linkrule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkRuleProcessor {

    private final TelegramChatLinkRuleService telegramChatLinkRuleService;

    public void process(List<LinkRuleOutboxDto> linkRuleOutboxDtos) {
        Map<OutboxEventType, List<LinkRuleOutboxDto>> groupedLinkRules = linkRuleOutboxDtos.stream()
                .collect(Collectors.groupingBy(LinkRuleOutboxDto::eventType));

        List<LinkRuleOutboxDto> upsertLinkRules = groupedLinkRules.getOrDefault(OutboxEventType.UPSERT, List.of());
        List<LinkRuleOutboxDto> removeLinkRules = groupedLinkRules.getOrDefault(OutboxEventType.REMOVE, List.of());

        telegramChatLinkRuleService.upsertLinkRules(upsertLinkRules);
        telegramChatLinkRuleService.deleteLinkRules(removeLinkRules);
    }
}
