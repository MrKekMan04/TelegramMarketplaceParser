package ru.overcode.scrapper.service.schedule.link;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.overcode.scrapper.BaseIntegrationTest;
import ru.overcode.scrapper.service.schedule.link.marketplace.WildberriesProcessor;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        properties = {
                "schedule.tasks.link.cron=* * * * * *"
        }
)
public class LinkSchedulerTest extends BaseIntegrationTest {

    @MockBean
    private WildberriesProcessor wildberriesProcessor;

    @AfterEach
    public void reset() {
        Mockito.reset(wildberriesProcessor);
    }

    @Test
    @DisplayName("Джоба запускается")
    public void processMarketplaces_shouldRun() {
        Awaitility.waitAtMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(wildberriesProcessor, atLeastOnce()).process());
    }
}
