package ru.overcode.scrapper.service.schedule.link.wildberries;

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
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.scrapper.BaseIntegrationTest;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse.DataPayload;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse.DataPayload.Product;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse.DataPayload.Product.Size;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse.DataPayload.Product.Size.Price;
import ru.overcode.scrapper.producer.LinkUpdateProducer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WildberriesProcessorTest extends BaseIntegrationTest {

    @RegisterExtension
    public static final WireMockExtension WIRE_MOCK_SERVER = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();
    private static final String PRODUCTS_API_URI = "/cards/v2/detail?curr=%s&dest=%s&nm=%s";

    @DynamicPropertySource
    public static void configureRegistry(DynamicPropertyRegistry registry) {
        registry.add("feign.wildberries.api.url", WIRE_MOCK_SERVER::baseUrl);
    }

    @Value("${feign.wildberries.api.curr}")
    private String curr;
    @Value("${feign.wildberries.api.dest}")
    private String dest;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WildberriesProcessor wildberriesProcessor;

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
        String nm = RandomStringUtils.randomNumeric(9);
        createLink(linkId, URI.create("https://www.wildberries.ru/catalog/" + nm));

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice / 100).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        WildberriesApiResponse apiResponse = new WildberriesApiResponse(new DataPayload(List.of(
                new Product(Long.valueOf(nm), List.of(
                        new Size(new Price(productPrice))
                ))
        )));

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(curr, dest, nm))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        wildberriesProcessor.process();

        verify(linkUpdateProducer).send(any());
    }

    @Test
    @DisplayName("Событие не шлётся в топик, когда не срабатывает правило меньше чем")
    public void process_shouldSkipEvent_whenLessThenAmountRuleNotTriggered() throws Exception {
        Long linkId = RandomUtils.nextLong();
        String nm = RandomStringUtils.randomNumeric(9);
        createLink(linkId, URI.create("https://www.wildberries.ru/catalog/" + nm));

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice / 100).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        WildberriesApiResponse apiResponse = new WildberriesApiResponse(new DataPayload(List.of(
                new Product(Long.valueOf(nm), List.of(
                        new Size(new Price(productPrice + 1))
                ))
        )));

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(curr, dest, nm))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        wildberriesProcessor.process();

        verify(linkUpdateProducer, never()).send(any());
    }

    @Test
    @DisplayName("Событие не шлётся в топик, когда не пришла цена")
    public void process_shouldSkipEvent_whenPriceIsNull() throws Exception {
        Long linkId = RandomUtils.nextLong();
        String nm = RandomStringUtils.randomNumeric(9);
        createLink(linkId, URI.create("https://www.wildberries.ru/catalog/" + nm));

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice / 100).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        WildberriesApiResponse apiResponse = new WildberriesApiResponse(new DataPayload(List.of(
                new Product(Long.valueOf(nm), List.of(
                        new Size(null)
                ))
        )));

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(curr, dest, nm))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        wildberriesProcessor.process();

        verify(linkUpdateProducer, never()).send(any());
    }

    @Test
    @DisplayName("Событие не шлётся в топик, когда пришёл пустой список продуктов")
    public void process_shouldSkipEvent_whenProductsListIsEmpty() throws Exception {
        Long linkId = RandomUtils.nextLong();
        String nm = RandomStringUtils.randomNumeric(9);
        createLink(linkId, URI.create("https://www.wildberries.ru/catalog/" + nm));

        long productPrice = 1000L;

        Long linkRuleId = RandomUtils.nextLong();
        BigDecimal triggerPrice = BigDecimal.valueOf(productPrice / 100).setScale(2, RoundingMode.HALF_UP);
        createLinkRule(linkRuleId, linkId, 1L, Map.of("amount", triggerPrice.toString()));

        WildberriesApiResponse apiResponse = new WildberriesApiResponse(new DataPayload(List.of()));

        WIRE_MOCK_SERVER.stubFor(WireMock.get(PRODUCTS_API_URI.formatted(curr, dest, nm))
                .willReturn(WireMock.ok()
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        wildberriesProcessor.process();

        verify(linkUpdateProducer, never()).send(any());
    }
}
