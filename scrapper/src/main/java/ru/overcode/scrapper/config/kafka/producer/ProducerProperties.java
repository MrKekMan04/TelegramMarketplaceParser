package ru.overcode.scrapper.config.kafka.producer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProducerProperties {

    private String topic;

    private Integer partitions;

    private Integer replicas;
}
