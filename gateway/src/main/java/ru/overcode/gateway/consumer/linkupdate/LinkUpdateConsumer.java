package ru.overcode.gateway.consumer.linkupdate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.overcode.gateway.exception.KafkaProcessingException;
import ru.overcode.gateway.service.linkupdate.LinkUpdateProcessor;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdateConsumer {

    private final LinkUpdateProcessor linkUpdateProcessor;

    @KafkaListener(
            containerFactory = "linkUpdateContainerFactory",
            topics = "${kafka.consumers.link-update.topic}",
            groupId = "${kafka.consumers.link-update.group-id}",
            autoStartup = "${kafka.consumers.link-update.enable}"
    )
    public void consume(ScrapperLinkUpdateDto record) {
        log.info("ScrapperLinkUpdateDto event received: {}", record);
        try {
            linkUpdateProcessor.process(record);
            log.info("ScrapperLinkUpdateDto event has been successfully processed: {}", record);
        } catch (Exception e) {
            throw new KafkaProcessingException("ScrapperLinkUpdateDto", record, e);
        }
    }
}
