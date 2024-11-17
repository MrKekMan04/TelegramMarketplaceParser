package ru.overcode.bot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
@EnableConfigurationProperties(ApplicationConfig.class)
public class FeignClientConfig {

}
