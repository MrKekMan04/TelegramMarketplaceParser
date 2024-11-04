package ru.overcode.gateway.service.schedule.chatlinkrule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component(LinkRuleOutboxScheduler.LINK_RULE_OUTBOX_TASK)
@RequiredArgsConstructor
public class LinkRuleOutboxScheduler {

    public static final String LINK_RULE_OUTBOX_TASK = "linkRuleOutboxTask";

    private final LinkRuleOutboxProcessor linkRuleOutboxProcessor;

    @Scheduled(cron = "${schedule.tasks.link-rule-outbox.cron}")
    @SchedulerLock(name = LINK_RULE_OUTBOX_TASK)
    public void sendLinkRuleOutbox() {
        log.info("Start {}", LINK_RULE_OUTBOX_TASK);
        try {
            linkRuleOutboxProcessor.process();
            log.info("End {}", LINK_RULE_OUTBOX_TASK);
        } catch (Exception e) {
            log.error("Error while processing {}", LINK_RULE_OUTBOX_TASK, e);
        }
    }
}
