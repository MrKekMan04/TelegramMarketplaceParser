package ru.overcode.scrapper.config.feign.wildberries;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.overcode.scrapper.dto.wildberries.WildberriesApiResponse;

public interface WildberriesFeignClient {

    @GetMapping("/cards/v2/detail")
    WildberriesApiResponse findProducts(
            @RequestParam String curr,
            @RequestParam String dest,
            @RequestParam String nm
    );
}
