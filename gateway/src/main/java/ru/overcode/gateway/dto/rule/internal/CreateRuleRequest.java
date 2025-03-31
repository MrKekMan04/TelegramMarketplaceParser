package ru.overcode.gateway.dto.rule.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateRuleRequest(

        @NotNull(message = "`id` не может быть пустым")
        @Positive(message = "`id` не может быть отрицательным")
        Long id,

        @NotBlank(message = "`name` не может быть пустым")
        String name,

        @NotBlank(message = "`description` не может быть пустым")
        String description
) {

}
