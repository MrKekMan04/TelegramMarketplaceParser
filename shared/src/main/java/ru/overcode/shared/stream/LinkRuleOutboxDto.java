package ru.overcode.shared.stream;

import ru.overcode.shared.dto.event.OutboxEventType;

import java.util.Map;

public record LinkRuleOutboxDto(
        Long id,
        Long linkId,
        Long ruleId,
        Map<String, String> params,
        OutboxEventType eventType
) {

}
