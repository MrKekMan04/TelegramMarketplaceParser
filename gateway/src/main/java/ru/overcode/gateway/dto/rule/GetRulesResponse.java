package ru.overcode.gateway.dto.rule;

import java.util.Set;

public record GetRulesResponse(
        Long ruleId,
        String description,
        Set<String> params
) {
}
