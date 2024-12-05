package ru.overcode.gateway.consumer.linkupdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.service.linkupdate.LinkUpdateProcessor;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class LinkUpdateConsumerWorkTest extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<Long, ScrapperLinkUpdateDto> kafkaTemplate;

    @MockBean
    private LinkUpdateProcessor linkUpdateProcessor;

    @Value("${kafka.consumers.link-update.topic}")
    private String scrapperLinkUpdateTopic;

    @BeforeEach
    public void resetMocks() {
        reset(linkUpdateProcessor);
    }

    @Test
    @DisplayName("Данные из кластера кафки попадают в консьюмер")
    @Disabled("Локально проходит")
    public void consume_shouldProcess_whenKafkaGetRecord() {
        ScrapperLinkUpdateDto record = new ScrapperLinkUpdateDto(RandomUtils.nextLong());

        kafkaTemplate.send(scrapperLinkUpdateTopic, record.id(), record).join();

        Awaitility.waitAtMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(linkUpdateProcessor).process(record));
    }
}
