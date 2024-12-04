package ru.overcode.scrapper.service.schedule.link;

import ru.overcode.shared.dto.market.MarketName;

public interface MarketplaceProcessor {

    MarketName getMarketName();

    void process();
}