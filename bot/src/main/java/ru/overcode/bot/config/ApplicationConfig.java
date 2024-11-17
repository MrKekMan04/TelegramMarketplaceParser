package ru.overcode.bot.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @NotEmpty
        String telegramToken,
        ApiLink apiLink,
        KafkaConfigInfo kafkaConfigInfo
) {
    public record ApiLink(String scrapper) {
    }

    public record KafkaConfigInfo(
            List<String> bootstrapServers,
            UpdatesTopic updatesTopic
    ) {
        public record UpdatesTopic(
                String name,
                Integer partitions,
                Integer replicas
        ) {
        }
    }
}