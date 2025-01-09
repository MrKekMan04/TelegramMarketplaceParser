package ru.overcode.bot.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.overcode.bot.config.kafka.consumer.ConsumerProperties;
import ru.overcode.shared.stream.update.GatewayLinkUpdateDto;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
@EnableKafka
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<Long, GatewayLinkUpdateDto> linkUpdateConsumerFactory() {
        ConsumerProperties properties = kafkaProperties.getConsumers().getLinkUpdate();
        return new DefaultKafkaConsumerFactory<>(getBaseConsumerConfig(GatewayLinkUpdateDto.class, properties));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, GatewayLinkUpdateDto> linkUpdateContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, GatewayLinkUpdateDto> listener
                = new ConcurrentKafkaListenerContainerFactory<>();
        listener.setConsumerFactory(linkUpdateConsumerFactory());
        return listener;
    }

    private Map<String, Object> getBaseConsumerConfig(Class<?> valueDefaultType, ConsumerProperties props) {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                JsonDeserializer.VALUE_DEFAULT_TYPE, valueDefaultType,
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG, props.getMaxPollRecords(),
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, props.getEnableAutoCommit(),
                ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, props.getMaxPollIntervalMs()
        );
    }
}
