package ru.overcode.scrapper.service.marketplace;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.scrapper.BaseIntegrationTest;
import ru.overcode.scrapper.model.link.Link;

import java.net.URI;
import java.util.List;

public class AliexpressServiceTest extends BaseIntegrationTest {

    @RegisterExtension
    public static final WireMockExtension WIRE_MOCK_SERVER = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();
    private static final String PRODUCTS_API_URI = "/aer-jsonapi/v1/bx/pdp/web/productData?productId=%s&sourceId=%s";

    @DynamicPropertySource
    public static void configureRegistry(DynamicPropertyRegistry registry) {
        registry.add("feign.aliexpress.api.url", WIRE_MOCK_SERVER::baseUrl);
    }

    @Autowired
    private AliexpressService aliexpressService;
    @Value("${feign.aliexpress.api.source-id}")
    private String sourceId;

    @Test
    @DisplayName("Запрос не ретраится")
    public void fetchProducts_shouldNeverRetry_whenExternalServiceUnavailable() {
        String productId = String.valueOf(RandomUtils.nextLong(0, 999));

        Link link = new Link()
                .setId(RandomUtils.nextLong())
                .setUrl(URI.create("https://aliexpress.ru/item/" + productId + ".html"));

        String url = PRODUCTS_API_URI.formatted(productId, sourceId);

        WIRE_MOCK_SERVER.stubFor(WireMock.get(url)
                .willReturn(WireMock.serviceUnavailable()));

        aliexpressService.fetchProducts(List.of(link));

        WIRE_MOCK_SERVER.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo(url)));
    }
}
