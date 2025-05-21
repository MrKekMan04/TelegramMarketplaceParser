package ru.overcode.scrapper.service.schedule.link.marketplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.scrapper.BaseIntegrationTest;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse.ProductData;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse.ProductData.SkuInfo;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse.ProductData.SkuInfo.Price;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse.ProductData.SkuInfo.Price.ActivityAmount;
import ru.overcode.scrapper.producer.LinkUpdateProducer;
import ru.overcode.shared.dto.market.MarketName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AliexpressProcessorTest extends BaseIntegrationTest {

    @RegisterExtension
    public static final WireMockExtension WIRE_MOCK_SERVER = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();
    private static final String PRODUCTS_API_URI = "/aer-jsonapi/v1/bx/pdp/web/productData?productId=%s&sourceId=%s";

    @DynamicPropertySource
    public static void configureRegistry(DynamicPropertyRegistry registry) {
        registry.add("feign.aliexpress.api.url", WIRE_MOCK_SERVER::baseUrl);
    }

    @Value("${feign.aliexpress.api.source-id}")
    private String sourceId;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AliexpressProcessor aliexpressProcessor;

    @MockBean
    private LinkUpdateProducer linkUpdateProducer;

    @AfterEach
    public void clearWireMock() {
        WIRE_MOCK_SERVER.resetAll();
        reset(linkUpdateProducer);
    }

    @Test
    @DisplayName("Событие шлётся в топик, когда срабатывает правило меньше чем")
    public void process_shouldSendInKafka_whenLessThenAmountRuleTriggered() throws Exception {
        Long linkId = RandomUtils.nextLong();
        String productId = String.valueOf(RandomUtils.nextLong(0, 99999));
        createLink(linkId, URI.create("https://aliexpress.ru/item/" + productId + ".html"), MarketName.ALIEXPRESS);

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        AliexpressApiResponse apiResponse = new AliexpressApiResponse(new ProductData(productId,
                        new SkuInfo(List.of(
                                new Price(new ActivityAmount(productPrice)))
                        ))
        );

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(productId, sourceId))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        aliexpressProcessor.process();

        verify(linkUpdateProducer).send(any());
    }

    @Test
    @DisplayName("Событие не шлётся в топик, когда не срабатывает правило меньше чем")
    public void process_shouldSkipEvent_whenLessThenAmountRuleNotTriggered() throws Exception {
        Long linkId = RandomUtils.nextLong();
        String productId = String.valueOf(RandomUtils.nextLong(0, 99999));
        createLink(linkId, URI.create("https://aliexpress.ru/item/" + productId + ".html"), MarketName.ALIEXPRESS);

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        AliexpressApiResponse apiResponse = new AliexpressApiResponse(new ProductData(productId,
                new SkuInfo(List.of(
                        new Price(new ActivityAmount(productPrice + 1)))
                ))
        );

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(productId, sourceId))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        aliexpressProcessor.process();

        verify(linkUpdateProducer, never()).send(any());
    }

    @Test
    @DisplayName("Событие не шлётся в топик, когда не пришла цена")
    public void process_shouldSkipEvent_whenPriceIsNull() throws Exception {
        Long linkId = RandomUtils.nextLong();
        String productId = String.valueOf(RandomUtils.nextLong(0, 99999));
        createLink(linkId, URI.create("https://aliexpress.ru/item/" + productId + ".html"), MarketName.ALIEXPRESS);

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        AliexpressApiResponse apiResponse = new AliexpressApiResponse(new ProductData(productId,
                new SkuInfo(List.of(
                        new Price(null))
                ))
        );

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(productId, sourceId))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        aliexpressProcessor.process();

        verify(linkUpdateProducer, never()).send(any());
    }

    @Test
    @DisplayName("Событие не шлётся в топик, когда не пришёл продукт")
    public void process_shouldSkipEvent_whenProductsListIsEmpty() throws Exception {
        Long linkId = RandomUtils.nextLong();
        String productId = String.valueOf(RandomUtils.nextLong(0, 99999));
        createLink(linkId, URI.create("https://aliexpress.ru/item/" + productId + ".html"), MarketName.ALIEXPRESS);

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        AliexpressApiResponse apiResponse = new AliexpressApiResponse(new ProductData(productId, new SkuInfo(List.of())));

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(productId, sourceId))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        aliexpressProcessor.process();

        verify(linkUpdateProducer, never()).send(any());
    }
}
