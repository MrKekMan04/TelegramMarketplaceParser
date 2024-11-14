package ru.overcode.scrapper.consumer.link;

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
import ru.overcode.scrapper.service.link.LinkProcessor;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.market.MarketName;
import ru.overcode.shared.stream.LinkOutboxDto;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class LinkConsumerWorkTest extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<Long, LinkOutboxDto> kafkaTemplate;

    @MockBean
    private LinkProcessor linkProcessor;

    @Value("${kafka.consumers.link-outbox.topic}")
    private String topic;

    @BeforeEach
    public void reset() {
        Mockito.reset(linkProcessor);
    }

    @Test
    @DisplayName("Получает сообщения из топика gateway.link.outbox")
    @Disabled("Не работает в mvn package")
    public void handleLinkOutboxDto_shouldConsume_whenKafkaGetEvent() {
        LinkOutboxDto linkRecord = new LinkOutboxDto(
                RandomUtils.nextLong(),
                URI.create("https://localhost.ru"),
                MarketName.WILDBERRIES,
                OutboxEventType.UPSERT
        );

        kafkaTemplate.send(topic, linkRecord.id(), linkRecord).join();

        Awaitility.waitAtMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(linkProcessor).process(any()));
    }
}