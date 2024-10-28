package ru.overcode.gateway.service.schedule.link;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component(LinkOutboxScheduler.LINK_OUTBOX_TASK)
@RequiredArgsConstructor
public class LinkOutboxScheduler {

    public static final String LINK_OUTBOX_TASK = "linkOutboxTask";

    private final LinkOutboxProcessor linkOutboxProcessor;

    @Scheduled(cron = "${schedule.tasks.link-outbox.cron}")
    @SchedulerLock(name = LINK_OUTBOX_TASK)
    public void sendLinkOutbox() {
        log.info("Start {}", LINK_OUTBOX_TASK);
        try {
            linkOutboxProcessor.process();
            log.info("End {}", LINK_OUTBOX_TASK);
        } catch (Exception e) {
            log.error("Error while processing {}", LINK_OUTBOX_TASK, e);
        }
    }
}
