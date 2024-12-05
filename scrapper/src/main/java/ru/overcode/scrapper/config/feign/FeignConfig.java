package ru.overcode.scrapper.config.feign;

import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.overcode.scrapper.config.feign.wildberries.WildberriesFeignClient;

@Configuration
public class FeignConfig {

    @Bean
    public WildberriesFeignClient wbFeignClient(
            @Value("${feign.wildberries.api.url}")
            String wildberriesApiUrl
    ) {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .retryer(Retryer.NEVER_RETRY)
                .logger(new Slf4jLogger(WildberriesFeignClient.class))
                .logLevel(Logger.Level.BASIC)
                .target(WildberriesFeignClient.class, wildberriesApiUrl);
    }
}
