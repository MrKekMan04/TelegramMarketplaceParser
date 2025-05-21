package ru.overcode.gateway.dto.market;

import ru.overcode.shared.dto.market.MarketName;

public record InternalMarketDto(
        Long id,
        MarketName name,
        String urlDomain
) {

}
