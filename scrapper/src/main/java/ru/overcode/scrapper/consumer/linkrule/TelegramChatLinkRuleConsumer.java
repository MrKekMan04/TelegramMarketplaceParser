package ru.overcode.scrapper.consumer.linkrule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.overcode.scrapper.service.linkrule.TelegramChatLinkRuleProcessor;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramChatLinkRuleConsumer {

    private final TelegramChatLinkRuleProcessor telegramChatLinkRuleProcessor;

    @KafkaListener(
            topics = "${kafka.consumers.link-rule-outbox.topic}",
            groupId = "${kafka.consumers.link-rule-outbox.group-id}",
            containerFactory = "linkRuleOutboxKafkaListenerContainerFactory"
    )
    public void handleLinkRuleOutboxDto(List<LinkRuleOutboxDto> linkRuleOutboxDtos) {
        log.info("Link rules have been received {}", linkRuleOutboxDtos);
        try {
            telegramChatLinkRuleProcessor.process(linkRuleOutboxDtos);
            log.info("Received link rules were successfully processed {}", linkRuleOutboxDtos);
        } catch (Exception exception) {
            log.error("Error occurred while processing link rules {}", linkRuleOutboxDtos, exception);
        }
    }
}
