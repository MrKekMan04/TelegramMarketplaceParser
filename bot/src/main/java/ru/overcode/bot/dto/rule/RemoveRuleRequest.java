package ru.overcode.bot.dto.rule;

import io.swagger.v3.oas.annotations.media.Schema;

public record RemoveRuleRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        Long chatId
) {
}
