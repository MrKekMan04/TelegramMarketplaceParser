package ru.overcode.gateway.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.overcode.shared.stream.LinkOutboxDto;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public NewTopic linkOutboxTopic() {
        return TopicBuilder
                .name(kafkaProperties.getProducers().getLinkOutbox().getTopic())
                .partitions(kafkaProperties.getProducers().getLinkOutbox().getPartitions())
                .replicas(kafkaProperties.getProducers().getLinkOutbox().getReplicas())
                .build();
    }

    @Bean
    public NewTopic linkRuleOutboxTopic() {
        return TopicBuilder
                .name(kafkaProperties.getProducers().getLinkRuleOutbox().getTopic())
                .partitions(kafkaProperties.getProducers().getLinkRuleOutbox().getPartitions())
                .replicas(kafkaProperties.getProducers().getLinkRuleOutbox().getReplicas())
                .build();
    }

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
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }
}
