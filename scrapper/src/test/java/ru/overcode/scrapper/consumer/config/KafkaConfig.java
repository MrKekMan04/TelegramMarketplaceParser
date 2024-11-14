package ru.overcode.scrapper.consumer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.overcode.scrapper.consumer.BaseIntegrationTest;
import ru.overcode.shared.stream.LinkOutboxDto;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.Map;

@TestConfiguration
public class KafkaConfig {

    @Bean
    public ProducerFactory<Long, LinkOutboxDto> linkOutboxProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getBaseConfig());
    }

    @Bean
    public KafkaTemplate<Long, LinkOutboxDto> linkOutboxKafkaTemplate() {
        return new KafkaTemplate<>(linkOutboxProducerFactory());
    }

    @Bean
    public ProducerFactory<Long, LinkRuleOutboxDto> linkRuleOutboxProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getBaseConfig());
    }

    @Bean
    public KafkaTemplate<Long, LinkRuleOutboxDto> linkRuleOutboxKafkaTemplate() {
        return new KafkaTemplate<>(linkRuleOutboxProducerFactory());
    }

    private Map<String, Object> getBaseConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BaseIntegrationTest.KAFKA.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }
}
