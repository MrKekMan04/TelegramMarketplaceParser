package ru.overcode.gateway.service.market;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.model.market.Market;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketDbService marketDbService;

    public Market findByIdOrElseThrow(Long marketId) {
        return marketDbService.findById(marketId)
                .orElseThrow(() -> new UnprocessableEntityException(
                        GatewayExceptionMessage.MARKET_NOT_FOUND
                                .withParam("marketId", marketId.toString())
                ));
    }
}
