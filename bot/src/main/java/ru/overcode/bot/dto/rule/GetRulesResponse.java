package ru.overcode.bot.dto.rule;

import java.util.Set;

public record GetRulesResponse(
        Long ruleId,
        String description,
        Set<String> params
) {
}
