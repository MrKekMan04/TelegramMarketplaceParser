package ru.overcode.scrapper.service.schedule.link;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component(LinkScheduler.LINK_TASK)
@RequiredArgsConstructor
public class LinkScheduler {

    public static final String LINK_TASK = "linkTask";

    private final List<MarketplaceProcessor> marketplaceProcessors;
    private final ExecutorService virtualThreadPool;

    @Scheduled(cron = "${schedule.tasks.link.cron}")
    @SchedulerLock(name = LINK_TASK)
    public void processMarketplaces() {
        log.info("Start {}", LINK_TASK);
        try {
            CompletableFuture.allOf(marketplaceProcessors.stream()
                            .map(processor -> CompletableFuture.runAsync(() -> processMarketplace(processor), virtualThreadPool))
                            .toArray(CompletableFuture[]::new))
                    .join();

            log.info("End {}", LINK_TASK);
        } catch (Exception e) {
            log.error("Error while processing {}", LINK_TASK, e);
        }
    }

    private void processMarketplace(MarketplaceProcessor processor) {
        log.info("Processing marketplace: {}", processor.getMarketName());
        try {
            processor.process();
            log.info("End marketplace process {}", processor.getMarketName());
        } catch (Exception e) {
            log.error("Error while processing marketplace: {}", processor.getMarketName(), e);
        }
    }
}
