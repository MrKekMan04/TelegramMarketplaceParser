package ru.overcode.gateway.service.market;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.dto.market.CreateMarketRequest;
import ru.overcode.gateway.dto.market.InternalMarketDto;
import ru.overcode.gateway.dto.market.MarketIdDto;
import ru.overcode.gateway.dto.market.UpdateMarketRequest;
import ru.overcode.gateway.dto.page.PagedRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.mapper.market.MarketMapper;
import ru.overcode.gateway.mapper.page.PagedRequestMapper;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.shared.api.PageContent;

@Service
@RequiredArgsConstructor
public class InternalMarketService {

    private final MarketService marketService;
    private final MarketDbService marketDbService;
    private final MarketMapper marketMapper;
    private final PagedRequestMapper pagedRequestMapper;

    public PageContent<InternalMarketDto> getMarkets(PagedRequest request) {
        Pageable pageable = pagedRequestMapper.toPageable(request);
        Page<Market> response = marketDbService.findAll(pageable);
        return new PageContent<>(
                response.getTotalPages(),
                marketMapper.toInternalMarketDto(response.getContent())
        );
    }

    @Transactional
    public MarketIdDto createMarket(CreateMarketRequest request) {
        marketDbService.findByName(request.name())
                .ifPresent(market -> {
                    throw new UnprocessableEntityException(
                            GatewayExceptionMessage.MARKET_EXISTS
                                    .withParam("marketName", market.getName().name())
                    );
                });

        Market savedEntity = marketDbService.save(marketMapper.toEntity(request));
        return marketMapper.toMarketIdDto(savedEntity);
    }

    @Transactional
    public void updateMarket(Long marketId, UpdateMarketRequest request) {
        Market market = marketService.findByIdOrElseThrow(marketId);

        marketMapper.fillEntity(market, request);
        marketDbService.save(market);
    }
}
