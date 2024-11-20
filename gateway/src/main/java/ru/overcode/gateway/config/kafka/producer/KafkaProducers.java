package ru.overcode.gateway.config.kafka.producer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaProducers {

    private ProducerProperties linkOutbox;

    private ProducerProperties linkRuleOutbox;

    private ProducerProperties linkUpdate;
}
