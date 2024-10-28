package ru.overcode.gateway.service.schedule.link;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.overcode.gateway.mapper.link.LinkMapper;
import ru.overcode.gateway.service.link.LinkOutboxDbService;
import ru.overcode.shared.dto.event.ProcessType;
import ru.overcode.shared.stream.LinkOutboxDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkOutboxProcessor {

    private final LinkOutboxDbService linkOutboxDbService;
    private final KafkaTemplate<Long, LinkOutboxDto> kafkaTemplate;
    private final LinkMapper linkMapper;

    @Value("${kafka.producers.link-outbox.topic}")
    private String linkOutboxTopic;

    public void process() {
        linkOutboxDbService.findAllByProcessType(ProcessType.PENDING)
                .forEach(outbox -> kafkaTemplate.send(linkOutboxTopic, outbox.getId(), linkMapper.toOutboxDto(outbox))
                        .whenComplete((result, exception) -> {
                            if (exception != null) {
                                log.error("Error while sending LinkOutbox with id {}", outbox.getId(), exception);
                            } else {
                                linkOutboxDbService.setProcessType(outbox.getId(), ProcessType.PROCESSED);
                                log.info("LinkOutbox with id {} processed successfully", outbox.getId());
                            }
                        }));
    }
}
