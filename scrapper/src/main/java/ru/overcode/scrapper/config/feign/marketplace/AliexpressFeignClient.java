package ru.overcode.scrapper.config.feign.marketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.overcode.scrapper.dto.marketplace.AliexpressApiResponse;

public interface AliexpressFeignClient {

    @GetMapping("/aer-jsonapi/v1/bx/pdp/web/productData")
    AliexpressApiResponse findProduct(
            @RequestParam String productId,
            @RequestParam String sourceId
    );
}
