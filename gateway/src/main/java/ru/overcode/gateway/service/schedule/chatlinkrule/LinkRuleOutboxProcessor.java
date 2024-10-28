package ru.overcode.gateway.service.schedule.chatlinkrule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.overcode.gateway.mapper.chatlink.rule.TelegramChatLinkRuleMapper;
import ru.overcode.gateway.service.chatlink.rule.TelegramChatLinkRuleOutboxDbService;
import ru.overcode.shared.dto.event.ProcessType;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkRuleOutboxProcessor {

    private final TelegramChatLinkRuleOutboxDbService outboxDbService;
    private final KafkaTemplate<Long, LinkRuleOutboxDto> kafkaTemplate;
    private final TelegramChatLinkRuleMapper bindingRuleMapper;

    @Value("${kafka.producers.link-rule-outbox.topic}")
    private String linkRuleOutboxTopic;

    public void process() {
        outboxDbService.findAllByProcessType(ProcessType.PENDING)
                .forEach(outbox -> kafkaTemplate.send(
                        linkRuleOutboxTopic,
                        outbox.getId(),
                        bindingRuleMapper.toOutboxDto(outbox)
                ).whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Error while sending LinkRuleOutbox with id {}", outbox.getId(), exception);
                    } else {
                        outboxDbService.setProcessType(outbox.getId(), ProcessType.PROCESSED);
                        log.info("LinkRuleOutbox with id {} processed successfully", outbox.getId());
                    }
                }));
    }
}
