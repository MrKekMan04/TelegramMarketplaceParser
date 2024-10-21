package ru.overcode.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.overcode.gateway.service.link.formatter.LinkFormatter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ApplicationConfig {

    @Bean
    public Map<String, LinkFormatter> linkFormatters(List<LinkFormatter> formatters) {
        return formatters.stream()
                .collect(Collectors.toMap(
                        LinkFormatter::getHost,
                        Function.identity()
                ));
    }
}
