package ru.overcode.scrapper.config.feign.marketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.overcode.scrapper.dto.marketplace.WildberriesApiResponse;

public interface WildberriesFeignClient {

    @GetMapping("/cards/v2/detail")
    WildberriesApiResponse findProducts(
            @RequestParam String curr,
            @RequestParam String dest,
            @RequestParam String nm
    );
}
