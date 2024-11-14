package ru.overcode.scrapper.consumer.link;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.overcode.scrapper.service.link.LinkProcessor;
import ru.overcode.shared.stream.LinkOutboxDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkConsumer {

    private final LinkProcessor linkProcessor;

    @KafkaListener(
            topics = "${kafka.consumers.link-outbox.topic}",
            groupId = "${kafka.consumers.link-outbox.group-id}",
            containerFactory = "linkOutboxKafkaListenerContainerFactory"
    )
    public void handleLinkOutboxDto(List<LinkOutboxDto> linkOutboxDtos) {
        log.info("Links have been received {}", linkOutboxDtos);
        try {
            linkProcessor.process(linkOutboxDtos);
            log.info("Received links were successfully processed {}", linkOutboxDtos);
        } catch (Exception exception) {
            log.error("Error occurred while processing links {}", linkOutboxDtos, exception);
        }
    }
}
