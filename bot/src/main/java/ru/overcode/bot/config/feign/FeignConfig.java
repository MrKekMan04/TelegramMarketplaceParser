package ru.overcode.bot.config.feign;

import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.overcode.bot.config.ApplicationConfig;
import ru.overcode.bot.config.feign.linkclient.LinkFeignClient;

@Configuration
@EnableConfigurationProperties(ApplicationConfig.class)
public class FeignConfig {

    @Bean
    public LinkFeignClient linkFeignClient(
            @Value("${app.gatewayUrl}")
            String gatewayUrl) {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .retryer(Retryer.NEVER_RETRY)
                .logger(new Slf4jLogger(LinkFeignClient.class))
                .logLevel(Logger.Level.FULL)
                .target(LinkFeignClient.class, gatewayUrl);
    }
}
