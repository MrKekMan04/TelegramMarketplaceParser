package ru.overcode.scrapper.service.wildberries;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.overcode.scrapper.config.feign.wildberries.WildberriesFeignClient;
import ru.overcode.scrapper.dto.ProductDto;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse;
import ru.overcode.scrapper.mapper.wildberries.WilberriesMapper;
import ru.overcode.scrapper.model.link.Link;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WildberriesService {

    private static final Pattern WILDBERRIES_PATTERN =
            Pattern.compile("^https://www\\.wildberries\\.ru/catalog/(?<itemId>\\d+)(/.*)?$");

    private final WildberriesFeignClient wildberriesFeignClient;
    private final WilberriesMapper wilberriesMapper;

    @Value("${feign.wildberries.api.curr}")
    private final String curr;
    @Value("${feign.wildberries.api.dest}")
    private final String dest;

    public Map<Long, ProductDto> fetchProducts(List<Link> links) {
        try {
            Map<Long, Long> linkIdByProductId = links.stream()
                    .collect(Collectors.toMap(
                            link -> Long.valueOf(getNumber(link.getUrl())),
                            Link::getId
                    ));

            String nmString = linkIdByProductId.keySet().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";"));

            WildberriesApiResponse response = wildberriesFeignClient.findProducts(curr, dest, nmString);
            return wilberriesMapper.fillWildberriesProductDtos(response, linkIdByProductId);
        } catch (Exception e) {
            log.error("Data could not be retrieved from the Wildberries API {}", e.getMessage(), e);
            return Map.of();
        }
    }

    private String getNumber(URI url) {
        Matcher matcher = WILDBERRIES_PATTERN.matcher(url.toString().toLowerCase());
        if (matcher.matches()) {
            return matcher.group("itemId");
        }
        throw new RuntimeException("URI was not matched " + url);
    }
}
