package ru.overcode.gateway.dto.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RemoveRuleRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`chatId` не может быть пустым")
        @Positive(message = "`chatId` не может быть отрицательным")
        Long chatId
) {
}
