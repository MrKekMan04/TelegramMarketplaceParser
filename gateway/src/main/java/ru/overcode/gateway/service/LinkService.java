package ru.overcode.gateway.service;

import org.springframework.stereotype.Service;
import ru.overcode.gateway.dto.link.AddLinkResponse;
import ru.overcode.gateway.dto.link.GetLinkResponse;

import java.net.URI;
import java.util.List;

@Service
public class LinkService {

    public List<GetLinkResponse> getLinks(Long chatId) {
        return List.of(
                new GetLinkResponse(1L, URI.create("http://localhost:8080"), List.of())
        );
    }

    public AddLinkResponse addLink(Long chatId, URI linkUrl) {
        return new AddLinkResponse(123L);
    }

    public void removeLink(Long chatId, Long linkId) {

    }
}
