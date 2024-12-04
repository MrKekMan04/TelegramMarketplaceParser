package ru.overcode.scrapper.service.link;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.overcode.scrapper.model.link.Link;
import ru.overcode.scrapper.repository.link.LinkRepository;
import ru.overcode.shared.dto.market.MarketName;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkDbService {

    private final LinkRepository linkRepository;

    @Transactional
    public void saveAll(List<Link> links) {
        linkRepository.saveAll(links);
    }

    @Transactional
    public void deleteAllByIdInBatch(Collection<Long> linkIds) {
        if (CollectionUtils.isEmpty(linkIds)) {
            return;
        }
        linkRepository.deleteAllByIdInBatch(linkIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, Link> findAllById(Collection<Long> linkIds) {
        if (CollectionUtils.isEmpty(linkIds)) {
            return Map.of();
        }
        return linkRepository.findAllById(linkIds).stream()
                .collect(Collectors.toMap(
                        Link::getId,
                        Function.identity()
                ));
    }

    @Transactional(readOnly = true)
    public Slice<Link> findLinksByMarketName(MarketName marketName, Pageable pageable) {
        return linkRepository.findLinksByMarketName(marketName, pageable);
    }
}
