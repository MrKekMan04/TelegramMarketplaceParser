package ru.overcode.gateway.service.link;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.repository.link.LinkRepository;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkDbService {

    private final LinkRepository linkRepository;

    @Transactional(readOnly = true)
    public Map<Long, Link> getLinksForChat(Long chatId) {
        return linkRepository.findAllByChatId(chatId).stream()
                .collect(Collectors.toMap(
                        Link::getId,
                        Function.identity()
                ));
    }

    @Transactional(readOnly = true)
    public Optional<Link> findById(Long linkId) {
        return linkRepository.findById(linkId);
    }

    @Transactional(readOnly = true)
    public Optional<Link> findByUrl(URI url) {
        return linkRepository.findByUrl(url);
    }

    @Transactional
    public Link createAndSave(URI linkUrl) {
        return null;
    }
}
