package ru.overcode.gateway.service.schedule.link;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.overcode.gateway.BaseIntegrationTest;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        properties = {
                "schedule.tasks.link-outbox.cron=* * * * * *"
        }
)
public class LinkOutboxSchedulerRunnerTest extends BaseIntegrationTest {

    @MockBean
    private LinkOutboxProcessor outboxProcessor;

    @AfterEach
    public void reset() {
        Mockito.reset(outboxProcessor);
    }

    @Test
    @DisplayName("Джоба запускается")
    public void sendLinkOutbox_shouldRun() {
        Awaitility.waitAtMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(outboxProcessor, atLeastOnce()).process());
    }
}
