package ru.overcode.gateway.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

import java.util.Map;

@TestConfiguration
public class KafkaTestConfig {

    @Bean
    public ProducerFactory<Long, ScrapperLinkUpdateDto> scrapperLinkUpdateProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getBaseProducerConfig());
    }

    @Bean
    public KafkaTemplate<Long, ScrapperLinkUpdateDto> scrapperLinkUpdateKafkaTemplate() {
        return new KafkaTemplate<>(scrapperLinkUpdateProducerFactory());
    }

    private Map<String, Object> getBaseProducerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BaseIntegrationTest.KAFKA.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }
}
