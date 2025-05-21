package ru.overcode.scrapper.mapper.marketplace;

import org.springframework.stereotype.Component;
import ru.overcode.scrapper.dto.ProductDto;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse.ProductData.SkuInfo;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse.ProductData.SkuInfo.Price.ActivityAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Optional;

@Component
public class AliexpressMapper {

    public ProductDto fillAliExpressProductDto(
            AliexpressApiResponse response
    ) {
        return Optional.ofNullable(response)
                .map(AliexpressApiResponse::data)
                .stream()
                .flatMap(productData -> Optional.ofNullable(productData.id())
                        .stream()
                        .flatMap(id -> Optional.ofNullable(productData.skuInfo())
                                .map(SkuInfo::priceList)
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(price -> Optional.ofNullable(price.activityAmount())
                                        .map(ActivityAmount::value)
                                        .isPresent())
                                .map(price -> new ProductDto()
                                        .setId(Long.valueOf(id))
                                        .setPrice(convertRublesToRublesWithPennies(price.activityAmount().value())))))
                .findFirst()
                .orElse(null);
    }

    private static BigDecimal convertRublesToRublesWithPennies(Long price) {
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}
