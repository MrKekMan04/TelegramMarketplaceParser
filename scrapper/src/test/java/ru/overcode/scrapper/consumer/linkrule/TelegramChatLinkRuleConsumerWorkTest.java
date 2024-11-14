package ru.overcode.scrapper.consumer.linkrule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.overcode.scrapper.consumer.BaseIntegrationTest;
import ru.overcode.scrapper.service.linkrule.TelegramChatLinkRuleProcessor;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class TelegramChatLinkRuleConsumerWorkTest extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<Long, LinkRuleOutboxDto> kafkaTemplate;

    @MockBean
    private TelegramChatLinkRuleProcessor telegramChatLinkRuleProcessor;

    @Value("${kafka.consumers.link-rule-outbox.topic}")
    private String topic;

    @BeforeEach
    public void reset() {
        Mockito.reset(telegramChatLinkRuleProcessor);
    }

    @Test
    @DisplayName("Получает сообщение из топика gateway.link-rule.outbox")
    @Disabled("Не работает в mvn package")
    public void handleLinkRuleOutboxDto_shouldConsume_whenKafkaGetEvent() {
        LinkRuleOutboxDto linkRuleRecord = new LinkRuleOutboxDto(
                RandomUtils.nextLong(),
                1L,
                2L,
                Map.of("key", "value"),
                OutboxEventType.UPSERT
        );

        kafkaTemplate.send(topic, linkRuleRecord.id(), linkRuleRecord).join();

        Awaitility.waitAtMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(telegramChatLinkRuleProcessor).process(any()));
    }
}
