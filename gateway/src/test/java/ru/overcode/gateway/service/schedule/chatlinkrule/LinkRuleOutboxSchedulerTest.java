package ru.overcode.gateway.service.schedule.chatlinkrule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRuleOutbox;
import ru.overcode.shared.dto.event.ProcessType;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinkRuleOutboxSchedulerTest extends BaseIntegrationTest {

    @Autowired
    private LinkRuleOutboxScheduler scheduler;

    @Test
    @DisplayName("Джоба должна отправлять информацию в кафку по ожидающим сущностям")
    public void sendLinkRuleOutbox_shouldSendToKafka_whenHasNotSentOutbox() {
        TelegramChatLinkRuleOutbox outbox = createLinkRuleOutbox();

        scheduler.sendLinkRuleOutbox();

        Awaitility.waitAtMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<TelegramChatLinkRuleOutbox> optionalSentOutbox =
                            bindingRuleOutboxRepository.findById(outbox.getId());

                    assertTrue(optionalSentOutbox.isPresent());
                    assertEquals(ProcessType.PROCESSED, optionalSentOutbox.get().getProcessType());
                });
    }
}
