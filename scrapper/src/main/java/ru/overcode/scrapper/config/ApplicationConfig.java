package ru.overcode.scrapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.overcode.scrapper.service.linkrule.rulecheckers.MarketplaceRuleChecker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ApplicationConfig {

    @Bean
    public Map<Long, MarketplaceRuleChecker> marketplaceRuleCheckersById(List<MarketplaceRuleChecker> validators) {
        return validators.stream()
                .collect(Collectors.toMap(
                        MarketplaceRuleChecker::getRuleId,
                        Function.identity()
                ));
    }

    @Bean
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean
    public ExecutorService virtualThreadPool() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
