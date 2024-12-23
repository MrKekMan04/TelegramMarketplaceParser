package ru.overcode.bot.dto.rule;

import io.swagger.v3.oas.annotations.media.Schema;

public record RuleDto(
        @Schema(description = "Внутренний идентификатор правила")
        Long ruleId,
        @Schema(description = "Описание правила для ссылки")
        String ruleDescription
) {

}
