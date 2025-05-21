package ru.overcode.gateway.service.market;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.dto.market.CreateMarketRequest;
import ru.overcode.gateway.dto.market.InternalMarketDto;
import ru.overcode.gateway.dto.market.UpdateMarketRequest;
import ru.overcode.gateway.dto.page.PagedRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.shared.api.PageContent;
import ru.overcode.shared.dto.market.MarketName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InternalMarketServiceTest extends BaseIntegrationTest {

    @Autowired
    private InternalMarketService internalMarketService;

    @Test
    @DisplayName("Маркетплейсы возвращаются в несортированном виде страницей, если сортировка не задана")
    public void getMarkets_shouldReturnUnsortedPage_whenSortIsNotConfigured() {
        PagedRequest pageable = new PagedRequest(0, 2, null);

        Market market = createMarket("www.wildberries.ru");

        PageContent<InternalMarketDto> pageContent = internalMarketService.getMarkets(pageable);

        assertEquals(1, pageContent.content().size());
        assertEquals(1, pageContent.totalPages());
        assertEquals(market.getName(), pageContent.content().getFirst().name());
        assertEquals(market.getUrlDomain(), pageContent.content().getFirst().urlDomain());
    }

    @Test
    @DisplayName("Маркетплейс успешно создается при валидных данных")
    public void createMarket_shouldCreateMarket_whenAllDataIsValid() {
        CreateMarketRequest request = new CreateMarketRequest(
                MarketName.WILDBERRIES,
                "www.wildberries.ru"
        );

        internalMarketService.createMarket(request);

        Optional<Market> marketOptional = marketRepository.findByName(MarketName.WILDBERRIES);
        assertTrue(marketOptional.isPresent());
        Market market = marketOptional.get();

        assertEquals(market.getUrlDomain(), request.urlDomain().toLowerCase());
    }

    @Test
    @DisplayName("Маркетплейс уже создан")
    public void createMarket_shouldThrow_whenMarketAlreadyCreated() {
        Market market = createMarket(MarketName.WILDBERRIES, "www.wildberries.ru");

        CreateMarketRequest request = new CreateMarketRequest(
                market.getName(),
                "www.newildberries.ru"
        );

        assertThrows(
                UnprocessableEntityException.class,
                () -> internalMarketService.createMarket(request),
                GatewayExceptionMessage.MARKET_EXISTS.getMessage()
                        .replace("{marketName}", market.getName().name())
        );
    }

    @Test
    @DisplayName("Маркетплейс успешно обновляется при валидных данных")
    public void updateMarket_shouldUpdateMarket_whenAllDataIsValid() {
        Market market = createMarket(MarketName.WILDBERRIES, "www.wildberries.ru");

        UpdateMarketRequest request = new UpdateMarketRequest(
                MarketName.WILDBERRIES,
                "www.newildberries.ru"
        );

        internalMarketService.updateMarket(market.getId(), request);

        Optional<Market> marketOptional = marketRepository.findById(market.getId());
        assertTrue(marketOptional.isPresent());
        Market updateMarket = marketOptional.get();

        assertEquals(updateMarket.getName(), request.name());
        assertEquals(updateMarket.getUrlDomain(), request.urlDomain().toLowerCase());
    }

    @Test
    @DisplayName("Маркетплейс не найден при обновлении")
    public void updateMarket_shouldThrow_whenMarketNotFound() {
        Long marketId = RandomUtils.nextLong();
        UpdateMarketRequest request = new UpdateMarketRequest(
                MarketName.WILDBERRIES,
                "www.newildberries.ru"
        );

        assertThrows(
                UnprocessableEntityException.class,
                () -> internalMarketService.updateMarket(marketId, request),
                GatewayExceptionMessage.MARKET_NOT_FOUND.getMessage()
                        .replace("{marketId}", marketId.toString())
        );
    }
}
