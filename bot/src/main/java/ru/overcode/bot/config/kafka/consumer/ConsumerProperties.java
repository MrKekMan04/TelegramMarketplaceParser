package ru.overcode.bot.config.kafka.consumer;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;

@Getter
@Setter
public class ConsumerProperties {

    private String topic;

    private String groupId;

    private Boolean enable;

    private Integer maxPollRecords = ConsumerConfig.DEFAULT_MAX_POLL_RECORDS;

    private Boolean enableAutoCommit = false;

    private Integer maxPollIntervalMs = 5000;
}
