package ru.overcode.scrapper.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdateProducer {

    private final KafkaTemplate<Long, ScrapperLinkUpdateDto> kafkaTemplate;
    @Value("${kafka.producers.link-update.topic}")
    private final String linkUpdateTopic;

    public void send(ScrapperLinkUpdateDto linkUpdate) {
        kafkaTemplate.send(linkUpdateTopic, linkUpdate.id(), linkUpdate)
                .whenComplete((sr, e) -> {
                    if (e == null) {
                        log.info("Link update has been sent: {}", linkUpdate);
                    } else {
                        log.error("Failed to send link update: {}", linkUpdate, e);
                    }
                });
    }
}
