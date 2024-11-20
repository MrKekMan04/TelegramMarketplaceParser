package ru.overcode.gateway.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.overcode.gateway.config.kafka.consumer.KafkaConsumers;
import ru.overcode.gateway.config.kafka.producer.KafkaProducers;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootstrapServers;

    private KafkaProducers producers;

    private KafkaConsumers consumers;
}


