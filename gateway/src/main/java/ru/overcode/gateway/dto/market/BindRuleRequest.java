package ru.overcode.gateway.dto.market;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BindRuleRequest(

        @NotNull(message = "`id` не может быть пустым")
        @Positive(message = "`id` не может быть отрицательным")
        Long id
) {
}
