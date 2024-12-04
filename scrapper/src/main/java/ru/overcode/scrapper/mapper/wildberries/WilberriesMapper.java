package ru.overcode.scrapper.mapper.wildberries;

import org.springframework.stereotype.Component;
import ru.overcode.scrapper.dto.ProductDto;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse.DataPayload;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse.DataPayload.Product.Size.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class WilberriesMapper {

    private static final BigDecimal RUBLES_DIVISOR = BigDecimal.valueOf(100);

    public Map<Long, ProductDto> fillWildberriesProductDtos(
            WildberriesApiResponse wildberriesApiResponse,
            Map<Long, Long> linkIdByProductId
    ) {
        return Optional.ofNullable(wildberriesApiResponse)
                .map(WildberriesApiResponse::data)
                .map(DataPayload::products)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(product -> Optional.ofNullable(product.id())
                        .stream()
                        .flatMap(id -> Optional.ofNullable(product.sizes())
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(size -> Optional.ofNullable(size.price())
                                        .map(Price::total)
                                        .isPresent())
                                .map(size -> new ProductDto()
                                        .setId(id)
                                        .setPrice(convertPenniesToRubles(size.price().total())))))
                .collect(Collectors.toMap(
                        dto -> linkIdByProductId.get(dto.getId()),
                        Function.identity(),
                        (existing, replacement) -> replacement
                ));
    }

    private static BigDecimal convertPenniesToRubles(Long priceInCents) {
        return BigDecimal.valueOf(priceInCents).divide(RUBLES_DIVISOR, 2, RoundingMode.HALF_UP);
    }
}
