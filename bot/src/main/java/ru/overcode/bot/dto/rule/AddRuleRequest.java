package ru.overcode.bot.dto.rule;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record AddRuleRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        Long chatId,
        @Schema(description = "Внутренний идентификатор правила", requiredMode = Schema.RequiredMode.REQUIRED)
        Long ruleId,
        @Schema(description = "Параметры для правила", requiredMode = Schema.RequiredMode.REQUIRED)
        Map<String, String> ruleParams
) {
}