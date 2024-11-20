package ru.overcode.gateway.config.kafka.consumer;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;

@Getter
@Setter
public class ConsumerProperties {

    private Integer maxPollRecords = ConsumerConfig.DEFAULT_MAX_POLL_RECORDS;

    private Boolean enableAutoCommit = false;

    private Integer maxPollIntervalMs = 5000;
}
