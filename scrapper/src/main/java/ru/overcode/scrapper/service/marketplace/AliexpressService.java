package ru.overcode.scrapper.service.marketplace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.overcode.scrapper.config.feign.marketplace.AliexpressFeignClient;
import ru.overcode.scrapper.dto.ProductDto;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse;
import ru.overcode.scrapper.mapper.marketplace.AliexpressMapper;
import ru.overcode.scrapper.model.link.Link;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AliexpressService {

    private static final Pattern ALIEXPRESS_PATTERN =
            Pattern.compile("^https://aliexpress\\.ru/item/(?<itemId>\\d+)\\.html(\\?.*)?$");

    private final AliexpressFeignClient aliexpressFeignClient;
    private final AliexpressMapper aliexpressMapper;
    private final ExecutorService virtualThreadPool;

    @Value("${feign.aliexpress.api.source-id}")
    private final String sourceId;

    public Map<Long, ProductDto> fetchProducts(List<Link> links) {
        Map<Long, ProductDto> productDtos = new ConcurrentHashMap<>();

        CompletableFuture.allOf(links.stream()
                .map(link -> CompletableFuture.runAsync(() -> {
                    Optional.ofNullable(getProductDtoForLink(link))
                            .ifPresent(dto -> productDtos.put(link.getId(), dto));
                }, virtualThreadPool))
                .toArray(CompletableFuture[]::new)
        ).join();

        return productDtos;
    }

    private ProductDto getProductDtoForLink(Link link) {
        try {
            AliexpressApiResponse response = aliexpressFeignClient.findProduct(getNumber(link.getUrl()), sourceId);
            return aliexpressMapper.fillAliExpressProductDto(response);
        } catch (Exception e) {
            log.error("Failed to process link {}", link.getId(), e);
            return null;
        }
    }

    private String getNumber(URI url) {
        Matcher matcher = ALIEXPRESS_PATTERN.matcher(url.toString().toLowerCase());
        if (matcher.matches()) {
            return matcher.group("itemId");
        }
        throw new RuntimeException("URI was not matched " + url);
    }
}
