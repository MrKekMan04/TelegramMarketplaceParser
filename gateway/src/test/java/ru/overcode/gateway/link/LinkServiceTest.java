package ru.overcode.gateway.link;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.StreamUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.dto.link.AddLinkResponse;
import ru.overcode.gateway.dto.link.GetLinkResponse;
import ru.overcode.gateway.dto.rule.RuleDto;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.gateway.service.link.LinkService;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class LinkServiceTest extends BaseIntegrationTest {

    private final String LINK_PREFIX = "https://www.host.com/link";
    @Autowired
    private LinkService linkService;

    @Test
    @DisplayName("Возвращаются отслеживаемые ссылки")
    public void getLinksWithRules_shouldReturnLinks_whenAllDataIsValid() {
        int linksCount = 10;
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);

        Stream<Link> expectedLinkOrder = IntStream.range(0, linksCount)
                .mapToObj(i -> URI.create(LINK_PREFIX + i))
                .map(this::createLink)
                .peek(link -> createBinding(chatId, link.getId()))
                .sorted(Comparator.comparing(Link::getId));

        Stream<GetLinkResponse> linksWithRules = linkService.getLinksWithRules(chatId).stream()
                .sorted(Comparator.comparing(GetLinkResponse::linkId));

        StreamUtils.zip(expectedLinkOrder, linksWithRules, (expected, actual) -> {
            assertEquals(expected.getId(), actual.linkId());
            assertEquals(expected.getUrl(), actual.linkUrl());
            assertEquals(0, actual.rules().size());
            return 0;
        });
    }

    @Test
    @DisplayName("Вместе с ссылкой возвращаются отслеживаемые для неё правила")
    public void getLinksWithRules_shouldReturnLinksWithRules_whenAllDataIsValid() {
        int rulesCount = 3;
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);
        Link link = createLink(URI.create(LINK_PREFIX));
        TelegramChatLink binding = createBinding(chatId, link.getId());
        Stream<Rule> expectedRulesOrder = Stream.generate(this::createRule)
                .limit(rulesCount)
                .peek(rule -> createBindingRule(binding.getId(), rule.getId(), Map.of()))
                .sorted(Comparator.comparing(Rule::getId));

        Stream<RuleDto> actualRulesSorted = linkService.getLinksWithRules(chatId).getFirst().rules().stream()
                .sorted(Comparator.comparing(RuleDto::ruleId));

        StreamUtils.zip(expectedRulesOrder, actualRulesSorted, (expected, actual) -> {
            assertEquals(expected.getId(), actual.ruleId());
            assertEquals(expected.getDescription(), actual.ruleDescription());
            return 0;
        });
    }

    @Test
    @DisplayName("Выбрасывается исключение при не найденном чате")
    public void getLinksWithRules_shouldThrow_whenChatNotFound() {
        Long chatId = RandomUtils.nextLong();

        assertThrows(UnprocessableEntityException.class, () -> linkService.getLinksWithRules(chatId));
    }

    @Test
    @DisplayName("Ссылка добавляется в отслеживаемые")
    public void addLink_shouldAdd_whenAllDataIsValid() {
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);
        URI url = URI.create(LINK_PREFIX);
        Market market = createMarket(url.getHost().toLowerCase());

        AddLinkResponse response = linkService.addLink(chatId, url);

        Optional<Link> optionalLink = linkRepository.findById(response.linkId());
        assertTrue(optionalLink.isPresent());
        assertEquals(market.getId(), optionalLink.get().getMarketId());

        Optional<TelegramChatLink> optionalBinding = bindingRepository
                .findByChatIdAndLinkId(chatId, response.linkId());
        assertTrue(optionalBinding.isPresent());
    }

    @Test
    @DisplayName("Выбрасывается исключение при не найденном чате")
    public void addLink_shouldThrow_whenChatNotFound() {
        Long chatId = RandomUtils.nextLong();

        assertThrows(UnprocessableEntityException.class, () -> linkService.addLink(chatId, URI.create(LINK_PREFIX)));
    }

    @Test
    @DisplayName("Выбрасывается исключение при не найденном маркетплейсе")
    public void addLink_shouldThrow_whenMarketNotFound() {
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);

        assertThrows(UnprocessableEntityException.class, () -> linkService.addLink(chatId, URI.create(LINK_PREFIX)));
    }

    @Test
    @DisplayName("Выбрасывается исключение при уже отслеживаемой ссылке")
    public void addLink_shouldThrow_whenLinkAlreadyAdded() {
        Long chatId = RandomUtils.nextLong();
        URI url = URI.create(LINK_PREFIX);

        createTelegramChat(chatId);
        Link link = createLink(url);
        createMarket(url.getHost());
        createBinding(chatId, link.getId());

        assertThrows(UnprocessableEntityException.class, () -> linkService.addLink(chatId, url));
    }

    @Test
    @DisplayName("Ссылка удаляется из отслеживаемых вместе с правилами")
    public void removeLink_shouldRemoveWithRules_whenAllDataIsValid() {
        Long chatId = RandomUtils.nextLong();
        URI url = URI.create(LINK_PREFIX);

        createTelegramChat(chatId);
        Link link = createLink(url);
        TelegramChatLink binding = createBinding(chatId, link.getId());
        Rule rule = createRule();
        TelegramChatLinkRule bindingRule = createBindingRule(binding.getId(), rule.getId(), Map.of());

        linkService.removeLink(chatId, link.getId());

        Optional<TelegramChatLink> optionalBinding = bindingRepository.findById(binding.getId());
        assertFalse(optionalBinding.isPresent());

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository.findById(bindingRule.getId());
        assertFalse(optionalBindingRule.isPresent());
    }

    @Test
    @DisplayName("Выбрасывается исключение при не найденном чате")
    public void removeLink_shouldThrow_whenChatNotFound() {
        Long chatId = RandomUtils.nextLong();

        Link link = createLink(URI.create(LINK_PREFIX));

        assertThrows(UnprocessableEntityException.class, () -> linkService.removeLink(chatId, link.getId()));
    }

    @Test
    @DisplayName("Выбрасывается исключение при не найденной ссылке")
    public void removeLink_shouldThrow_whenLinkNotFound() {
        Long chatId = RandomUtils.nextLong();
        Long linkId = RandomUtils.nextLong();

        createTelegramChat(chatId);

        assertThrows(UnprocessableEntityException.class, () -> linkService.removeLink(chatId, linkId));
    }

    @Test
    @DisplayName("Выбрасывается исключение при не отслеживаемой ссылке")
    public void removeLink_shouldThrow_whenLinkNotAdded() {
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);
        Link link = createLink(URI.create(LINK_PREFIX));

        assertThrows(UnprocessableEntityException.class, () -> linkService.removeLink(chatId, link.getId()));
    }
}
