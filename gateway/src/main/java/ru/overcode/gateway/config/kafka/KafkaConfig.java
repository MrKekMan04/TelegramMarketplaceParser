package ru.overcode.gateway.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.overcode.shared.stream.LinkOutboxDto;
import ru.overcode.shared.stream.LinkRuleOutboxDto;
import ru.overcode.shared.stream.update.GatewayLinkUpdateDto;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
@EnableKafka
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
    public NewTopic linkUpdateTopic() {
        return TopicBuilder
                .name(kafkaProperties.getProducers().getLinkUpdate().getTopic())
                .partitions(kafkaProperties.getProducers().getLinkUpdate().getPartitions())
                .replicas(kafkaProperties.getProducers().getLinkUpdate().getReplicas())
                .build();
    }

    @Bean
    public ConsumerFactory<Long, ScrapperLinkUpdateDto> linkUpdateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getBaseConsumerConfig(ScrapperLinkUpdateDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, ScrapperLinkUpdateDto> linkUpdateContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, ScrapperLinkUpdateDto> listener
                = new ConcurrentKafkaListenerContainerFactory<>();
        listener.setConsumerFactory(linkUpdateConsumerFactory());
        return listener;
    }

    @Bean
    public ProducerFactory<Long, LinkOutboxDto> linkOutboxProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getBaseProducerConfig());
    }

    @Bean
    public KafkaTemplate<Long, LinkOutboxDto> linkOutboxKafkaTemplate() {
        return new KafkaTemplate<>(linkOutboxProducerFactory());
    }

    @Bean
    public ProducerFactory<Long, LinkRuleOutboxDto> linkRuleOutboxProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getBaseProducerConfig());
    }

    @Bean
    public KafkaTemplate<Long, LinkRuleOutboxDto> linkRuleOutboxKafkaTemplate() {
        return new KafkaTemplate<>(linkRuleOutboxProducerFactory());
    }

    @Bean
    public ProducerFactory<Long, GatewayLinkUpdateDto> linkUpdateProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getBaseProducerConfig());
    }

    @Bean
    public KafkaTemplate<Long, GatewayLinkUpdateDto> linkUpdateKafkaTemplate() {
        return new KafkaTemplate<>(linkUpdateProducerFactory());
    }

    private Map<String, Object> getBaseProducerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }

    private Map<String, Object> getBaseConsumerConfig(Class<?> valueDefaultType) {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                JsonDeserializer.VALUE_DEFAULT_TYPE, valueDefaultType
        );
    }
}
