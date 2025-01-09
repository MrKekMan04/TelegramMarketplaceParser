package ru.overcode.bot.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.overcode.bot.config.kafka.consumer.KafkaConsumers;


@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootstrapServers;

    private KafkaConsumers consumers;
}

