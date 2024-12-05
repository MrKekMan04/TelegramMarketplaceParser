package ru.overcode.scrapper.service.link;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.scrapper.mapper.link.LinkMapper;
import ru.overcode.scrapper.model.link.Link;
import ru.overcode.shared.dto.market.MarketName;
import ru.overcode.shared.stream.LinkOutboxDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkDbService linkDbService;
    private final LinkMapper linkMapper;

    @Transactional
    public void upsertLinks(List<LinkOutboxDto> linkOutboxDtos) {
        Map<Long, LinkOutboxDto> dtoById = linkOutboxDtos.stream()
                .collect(Collectors.toMap(
                        LinkOutboxDto::id,
                        Function.identity()
                ));

        Map<Long, Link> existingEntities = linkDbService.findAllById(dtoById.keySet());

        List<Link> links = dtoById.keySet().stream()
                .map(id -> existingEntities.getOrDefault(id, new Link().setId(id)))
                .peek(link -> linkMapper.fillLink(link, dtoById.get(link.getId())))
                .toList();

        linkDbService.saveAll(links);
    }

    public void deleteLinks(List<LinkOutboxDto> linkOutboxDtos) {
        Set<Long> ids = linkOutboxDtos.stream()
                .map(LinkOutboxDto::id)
                .collect(Collectors.toSet());

        linkDbService.deleteAllByIdInBatch(ids);
    }

    public Slice<Link> findLinksByMarketName(MarketName marketName, Pageable pageable) {
        return linkDbService.findLinksByMarketName(marketName, pageable);
    }
}
