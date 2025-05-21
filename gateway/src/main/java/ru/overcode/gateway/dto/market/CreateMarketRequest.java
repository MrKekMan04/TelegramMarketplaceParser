package ru.overcode.gateway.dto.market;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.overcode.shared.dto.market.MarketName;

public record CreateMarketRequest(
        @NotNull(message = "`name` не может быть пустым")
        MarketName name,
        @NotBlank(message = "`urlDomain` не может быть пустым")
        String urlDomain
) {
}
