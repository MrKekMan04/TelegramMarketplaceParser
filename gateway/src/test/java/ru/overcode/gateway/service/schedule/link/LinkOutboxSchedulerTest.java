package ru.overcode.gateway.service.schedule.link;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.model.link.LinkOutbox;
import ru.overcode.shared.dto.event.ProcessType;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinkOutboxSchedulerTest extends BaseIntegrationTest {

    @Autowired
    private LinkOutboxScheduler scheduler;

    @Test
    @DisplayName("Джоба должна отправлять информацию в кафку по ожидающим сущностям")
    public void sendLinkOutbox_shouldSendToKafka_whenHasNotSentOutbox() {
        LinkOutbox outbox = createLinkOutbox();

        scheduler.sendLinkOutbox();

        Awaitility.waitAtMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<LinkOutbox> optionalSentOutbox = linkOutboxRepository.findById(outbox.getId());

                    assertTrue(optionalSentOutbox.isPresent());
                    assertEquals(ProcessType.PROCESSED, optionalSentOutbox.get().getProcessType());
                });
    }
}
