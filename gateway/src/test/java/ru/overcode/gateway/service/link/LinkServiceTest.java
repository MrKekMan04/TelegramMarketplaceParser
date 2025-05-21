package ru.overcode.gateway.service.link;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import ru.overcode.gateway.model.link.LinkOutbox;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.event.ProcessType;
import ru.overcode.shared.dto.market.MarketName;

import java.net.URI;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class LinkServiceTest extends BaseIntegrationTest {

    private static final Map<MarketName, String> URI_BY_MARKET_NAME = Map.of(
            MarketName.WILDBERRIES, "https://www.wildberries.ru/catalog/1%d",
            MarketName.ALIEXPRESS, "https://aliexpress.ru/item/1%d.html"
    );

    @Autowired
    private LinkService linkService;

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Возвращаются отслеживаемые ссылки")
    public void getLinksWithRules_shouldReturnLinks_whenAllDataIsValid(MarketName marketName) {
        int linksCount = 10;
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);

        Stream<Link> expectedLinkOrder = IntStream.range(0, linksCount)
                .mapToObj(i -> URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(i)))
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

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Вместе с ссылкой возвращаются отслеживаемые для неё правила")
    public void getLinksWithRules_shouldReturnLinksWithRules_whenAllDataIsValid(MarketName marketName) {
        int rulesCount = 3;
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);
        Link link = createLink(URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1)));
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

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("""
            Ссылка добавляется в отслеживаемые.
            Также реплицируется в outbox, если до этого не была отправлена
            """)
    public void addLink_shouldAdd_whenAllDataIsValid(MarketName marketName) {
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);
        URI url = URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1));
        Market market = createMarket(marketName, url.getHost().toLowerCase());

        AddLinkResponse response = linkService.addLink(chatId, url);

        Optional<Link> optionalLink = linkRepository.findById(response.linkId());
        assertTrue(optionalLink.isPresent());
        assertEquals(market.getId(), optionalLink.get().getMarketId());

        Optional<TelegramChatLink> optionalBinding = bindingRepository
                .findByChatIdAndLinkId(chatId, response.linkId());
        assertTrue(optionalBinding.isPresent());

        List<LinkOutbox> optionalOutbox = linkOutboxRepository.findAllByLinkId(optionalLink.get().getId());
        assertEquals(1, optionalOutbox.size());
        assertEquals(ProcessType.PENDING, optionalOutbox.getFirst().getProcessType());
        assertEquals(OutboxEventType.UPSERT, optionalOutbox.getFirst().getEventType());
    }

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Выбрасывается исключение при не найденном чате")
    public void addLink_shouldThrow_whenChatNotFound(MarketName marketName) {
        Long chatId = RandomUtils.nextLong();

        assertThrows(
                UnprocessableEntityException.class,
                () -> linkService.addLink(chatId, URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1)))
        );
    }

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Выбрасывается исключение при не найденном маркетплейсе")
    public void addLink_shouldThrow_whenMarketNotFound(MarketName marketName) {
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);

        assertThrows(
                UnprocessableEntityException.class,
                () -> linkService.addLink(chatId, URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1)))
        );
    }

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Выбрасывается исключение при уже отслеживаемой ссылке")
    public void addLink_shouldThrow_whenLinkAlreadyAdded(MarketName marketName) {
        Long chatId = RandomUtils.nextLong();
        URI url = URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1));

        createTelegramChat(chatId);
        Link link = createLink(url);
        createMarket(marketName, url.getHost());
        createBinding(chatId, link.getId());

        assertThrows(UnprocessableEntityException.class, () -> linkService.addLink(chatId, url));
    }

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Ссылка удаляется из отслеживаемых вместе с правилами")
    public void removeLink_shouldRemoveWithRules_whenAllDataIsValid(MarketName marketName) {
        Long chatId = RandomUtils.nextLong();
        URI url = URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1));

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

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Выбрасывается исключение при не найденном чате")
    public void removeLink_shouldThrow_whenChatNotFound(MarketName marketName) {
        Long chatId = RandomUtils.nextLong();

        Link link = createLink(URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1)));

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

    @ParameterizedTest
    @MethodSource("possibleMarkets")
    @DisplayName("Выбрасывается исключение при не отслеживаемой ссылке")
    public void removeLink_shouldThrow_whenLinkNotAdded(MarketName marketName) {
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);
        Link link = createLink(URI.create(URI_BY_MARKET_NAME.get(marketName).formatted(1)));

        assertThrows(UnprocessableEntityException.class, () -> linkService.removeLink(chatId, link.getId()));
    }

    private static Stream<Arguments> possibleMarkets() {
        return Arrays.stream(MarketName.values())
                .map(Arguments::of);
    }
}
