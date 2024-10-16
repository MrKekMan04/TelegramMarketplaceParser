package ru.overcode.gateway.dto.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record AddRuleRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`chatId` не может быть пустым")
        @Positive(message = "`chatId` не может быть отрицательным")
        Long chatId,
        @Schema(description = "Внутренний идентификатор правила", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`ruleId` не может быть пустым")
        @Positive(message = "`ruleId` не может быть отрицательным")
        Long ruleId,
        @Schema(description = "Параметры для правила", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`ruleParams` не может быть пустым")
        @Size(max = 50, message = "Размер `ruleParams` - максимум {max}")
        Map<String, String> ruleParams
) {
}
