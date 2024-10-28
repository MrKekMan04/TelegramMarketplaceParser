package ru.overcode.shared.stream;

import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.market.MarketName;

import java.net.URI;

public record LinkOutboxDto(
        Long id,
        URI url,
        MarketName marketName,
        OutboxEventType eventType
) {

}
