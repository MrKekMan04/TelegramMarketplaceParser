package ru.overcode.scrapper.consumer.link;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.scrapper.BaseIntegrationTest;
import ru.overcode.scrapper.model.link.Link;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.market.MarketName;
import ru.overcode.shared.stream.LinkOutboxDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LinkConsumerTest extends BaseIntegrationTest {

    @Autowired
    private LinkConsumer linkConsumer;

    @Test
    @DisplayName("Ссылка сохраняется в таблицу link")
    public void handleLinkOutboxDto_shouldSave_whenLinkDoesNotExist() {
        List<LinkOutboxDto> linkOutboxDtos = List.of(
                new LinkOutboxDto(
                        RandomUtils.nextLong(),
                        URI.create("https://localhost.ru"),
                        MarketName.WILDBERRIES,
                        OutboxEventType.UPSERT
                )
        );

        linkConsumer.handleLinkOutboxDto(linkOutboxDtos);

        Optional<Link> link = linkRepository.findById(linkOutboxDtos.getFirst().id());

        assertTrue(link.isPresent());
        assertEquals(linkOutboxDtos.getFirst().url(), link.get().getUrl());
        assertEquals(linkOutboxDtos.getFirst().marketName(), link.get().getMarketName());
    }

    @Test
    @DisplayName("Ссылка обновляется в таблице link")
    public void handleLinkOutboxDto_shouldUpdate_whenLinkExists() {
        Long linkId = RandomUtils.nextLong();
        createLink(linkId, URI.create("https://test.ru"));

        List<LinkOutboxDto> linkOutboxDtos = List.of(
                new LinkOutboxDto(
                        linkId,
                        URI.create("https://localhost.ru"),
                        MarketName.WILDBERRIES,
                        OutboxEventType.UPSERT
                )
        );

        linkConsumer.handleLinkOutboxDto(linkOutboxDtos);

        Optional<Link> link = linkRepository.findById(linkOutboxDtos.getFirst().id());

        assertTrue(link.isPresent());
        assertEquals(linkOutboxDtos.getFirst().url(), link.get().getUrl());
        assertEquals(linkOutboxDtos.getFirst().marketName(), link.get().getMarketName());
    }

    @Test
    @DisplayName("Ссылка удаляется из таблицы link")
    public void handleLinkOutboxDto_shouldDelete_whenLinkExists() {
        Long linkId = RandomUtils.nextLong();
        createLink(linkId, URI.create("https://test.ru"));

        List<LinkOutboxDto> linkOutboxDtos = List.of(
                new LinkOutboxDto(
                        linkId,
                        null,
                        null,
                        OutboxEventType.REMOVE
                )
        );

        linkConsumer.handleLinkOutboxDto(linkOutboxDtos);

        Optional<Link> link = linkRepository.findById(linkOutboxDtos.getFirst().id());

        assertFalse(link.isPresent());
    }
}
