package ru.overcode.scrapper.config.kafka;

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
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
@EnableKafka
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public NewTopic linkUpdateTopic() {
        return TopicBuilder
                .name(kafkaProperties.getProducers().getLinkUpdate().getTopic())
                .partitions(kafkaProperties.getProducers().getLinkUpdate().getPartitions())
                .replicas(kafkaProperties.getProducers().getLinkUpdate().getReplicas())
                .build();
    }

    @Bean
    public ConsumerFactory<Long, LinkOutboxDto> linkOutboxConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(LinkOutboxDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkOutboxDto> linkOutboxKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, LinkOutboxDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(linkOutboxConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, LinkRuleOutboxDto> linkRuleOutboxConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(LinkRuleOutboxDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkRuleOutboxDto> linkRuleOutboxKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, LinkRuleOutboxDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(linkRuleOutboxConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ProducerFactory<Long, ScrapperLinkUpdateDto> linkUpdateProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getProducerConfig());
    }

    @Bean
    public KafkaTemplate<Long, ScrapperLinkUpdateDto> linkUpdateKafkaTemplate() {
        return new KafkaTemplate<>(linkUpdateProducerFactory());
    }

    private Map<String, Object> getProducerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }

    private Map<String, Object> getConsumerConfig(Class<?> deserializerType) {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                JsonDeserializer.VALUE_DEFAULT_TYPE, deserializerType
        );
    }
}
