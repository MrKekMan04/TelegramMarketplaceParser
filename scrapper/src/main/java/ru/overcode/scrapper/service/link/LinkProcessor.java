package ru.overcode.scrapper.service.link;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.stream.LinkOutboxDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkProcessor {

    private final LinkService linkService;

    public void process(List<LinkOutboxDto> linkOutboxDtos) {
        Map<OutboxEventType, List<LinkOutboxDto>> groupedLinks = linkOutboxDtos.stream()
                .collect(Collectors.groupingBy(LinkOutboxDto::eventType));

        List<LinkOutboxDto> upsertLinks = groupedLinks.getOrDefault(OutboxEventType.UPSERT, List.of());
        List<LinkOutboxDto> removeLinks = groupedLinks.getOrDefault(OutboxEventType.REMOVE, List.of());

        linkService.upsertLinks(upsertLinks);
        linkService.deleteLinks(removeLinks);
    }
}
