package ru.overcode.bot.dto.rule;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record GetRulesResponse(
        @Schema(description = "Внутренний идентификатор правила", requiredMode = Schema.RequiredMode.REQUIRED)
        Long ruleId,
        @Schema(description = "Описание правила для ссылки", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
        @Schema(description = "Параметры для правила", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<String> params
) {
}
