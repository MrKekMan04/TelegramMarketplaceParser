package ru.overcode.gateway.dto.rule.internal;

import jakarta.validation.constraints.NotBlank;

public record UpdateRuleRequest(

        @NotBlank(message = "`name` не может быть пустым")
        String name,

        @NotBlank(message = "`description` не может быть пустым")
        String description
) {

}
