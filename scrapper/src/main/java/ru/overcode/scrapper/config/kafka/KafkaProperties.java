package ru.overcode.scrapper.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.overcode.scrapper.config.kafka.producer.KafkaProducers;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootstrapServers;

    private KafkaProducers producers;
}
