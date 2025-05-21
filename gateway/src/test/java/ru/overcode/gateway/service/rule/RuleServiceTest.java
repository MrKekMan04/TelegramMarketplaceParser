package ru.overcode.gateway.service.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.StreamUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.dto.rule.GetRulesResponse;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRuleOutbox;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.event.ProcessType;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RuleServiceTest extends BaseIntegrationTest {

    private static final String URL = "https://host.ru";

    @Autowired
    private RuleService ruleService;

    @Test
    @DisplayName("Возвращаются правила для ссылки при корерктных данных")
    public void getRules_shouldReturnRulesForLink_whenAllDataIsValid() {
        URI url = URI.create(URL);
        Long ruleId = 1L;
        Market market = createMarket(url.getHost());
        Link link = createLink(url, market.getId());
        createRule(ruleId);
        createMarketRule(market.getId(), ruleId);

        List<Long> expectedRules = Stream.of(ruleId)
                .toList();

        List<GetRulesResponse> sortedResponse = ruleService.getRules(link.getId()).stream()
                .sorted(Comparator.comparing(GetRulesResponse::ruleId))
                .toList();

        assertEquals(expectedRules.size(), sortedResponse.size());

        StreamUtils.zip(expectedRules.stream(), sortedResponse.stream(), (expectedId, actualRule) -> {
            assertEquals(expectedId, actualRule.ruleId());
            return 0;
        });
    }

    @Test
    @DisplayName("Выбрасывается исключение при несуществующей ссылке")
    public void getRules_shouldThrow_whenLinkNotFound() {
        Long linkId = RandomUtils.nextLong();

        assertThrows(UnprocessableEntityException.class, () -> ruleService.getRules(linkId));
    }

    @Test
    @DisplayName("""
            Правило добавляется в отслеживаемые.
            Также реплицируется в outbox
            """)
    public void addRule_shouldAdd_whenAllDataIsValid() {
        URI url = URI.create(URL);
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Market market = createMarket(url.getHost());
        Link link = createLink(url, market.getId());
        createRule(ruleId);
        createMarketRule(market.getId(), ruleId);
        TelegramChatLink binding = createBinding(chatId, link.getId());

        ruleService.addRule(chatId, link.getId(), ruleId, Map.of("amount", "513"));

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository
                .findByChatLinkIdAndRuleId(binding.getId(), ruleId);
        assertTrue(optionalBindingRule.isPresent());

        List<TelegramChatLinkRuleOutbox> optionalOutbox =
                bindingRuleOutboxRepository.findAllByTelegramChatLinkRuleId(optionalBindingRule.get().getId());
        assertEquals(1, optionalOutbox.size());
        assertEquals(ProcessType.PENDING, optionalOutbox.getFirst().getProcessType());
        assertEquals(OutboxEventType.UPSERT, optionalOutbox.getFirst().getEventType());
    }

    @Test
    @DisplayName("Выбрасывается исключение при несуществующем чате")
    public void addRule_shouldThrow_whenChatNotFound() {
        URI url = URI.create(URL);
        Long ruleId = 1L;
        Long chatId = RandomUtils.nextLong();
        Market market = createMarket(url.getHost());
        Link link = createLink(url, market.getId());
        createRule(ruleId);
        createMarketRule(market.getId(), ruleId);
        TelegramChatLink binding = createBinding(chatId, link.getId());

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.addRule(chatId, link.getId(), ruleId, Map.of("amount", "513")));

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository
                .findByChatLinkIdAndRuleId(binding.getId(), ruleId);
        assertFalse(optionalBindingRule.isPresent());
    }

    @Test
    @DisplayName("Выбрасывается исключение при несуществующей ссылке")
    public void addRule_shouldThrow_whenLinkNotFound() {
        URI url = URI.create(URL);
        Long chatId = RandomUtils.nextLong();
        Long linkId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Market market = createMarket(url.getHost());
        createRule(ruleId);
        createMarketRule(market.getId(), ruleId);
        TelegramChatLink binding = createBinding(chatId, linkId);

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.addRule(chatId, linkId, ruleId, Map.of("amount", "513")));

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository
                .findByChatLinkIdAndRuleId(binding.getId(), ruleId);
        assertFalse(optionalBindingRule.isPresent());
    }

    @Test
    @DisplayName("Выбрасывается исключение при не отслеживаемой ссылке")
    public void addRule_shouldThrow_whenLinkNotAdded() {
        URI url = URI.create(URL);
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Market market = createMarket(url.getHost());
        Link link = createLink(url, market.getId());
        createRule(ruleId);
        createMarketRule(market.getId(), ruleId);

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.addRule(chatId, link.getId(), ruleId, Map.of("amount", "513")));
    }

    @Test
    @DisplayName("Выбрасывается исключение при не подходящем правиле для ссылки")
    public void addRule_shouldThrow_whenMarketRuleNotFound() {
        URI url = URI.create(URL);
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Market market = createMarket(url.getHost());
        Link link = createLink(url, market.getId());
        createRule(ruleId);
        TelegramChatLink binding = createBinding(chatId, link.getId());

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.addRule(chatId, link.getId(), ruleId, Map.of("amount", "513")));

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository
                .findByChatLinkIdAndRuleId(binding.getId(), ruleId);
        assertFalse(optionalBindingRule.isPresent());
    }

    @Test
    @DisplayName("Выбрасывается исключение при некорректных параметрах для правила")
    public void addRule_shouldThrow_whenRuleBadParams() {
        URI url = URI.create(URL);
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Market market = createMarket(url.getHost());
        Link link = createLink(url, market.getId());
        createRule(ruleId);
        TelegramChatLink binding = createBinding(chatId, link.getId());
        createMarketRule(market.getId(), ruleId);

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.addRule(chatId, link.getId(), ruleId, Map.of()));

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository
                .findByChatLinkIdAndRuleId(binding.getId(), ruleId);
        assertFalse(optionalBindingRule.isPresent());
    }

    @Test
    @DisplayName("Выбрасывается исключение при уже отслеживаемом правиле для ссылки")
    public void addRule_shouldThrow_whenRuleAlreadyAdded() {
        URI url = URI.create(URL);
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Market market = createMarket(url.getHost());
        Link link = createLink(url, market.getId());
        createRule(ruleId);
        TelegramChatLink binding = createBinding(chatId, link.getId());
        createMarketRule(market.getId(), ruleId);
        createBindingRule(binding.getId(), ruleId, Map.of("amount", "513"));

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.addRule(chatId, link.getId(), ruleId, Map.of()));

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository
                .findByChatLinkIdAndRuleId(binding.getId(), ruleId);
        assertTrue(optionalBindingRule.isPresent());
    }

    @Test
    @DisplayName("""
            Правило удаляется из отслеживаемых.
            Также реплицируется в outbox
            """)
    public void removeRule_shouldRemove_whenAllDataIsValid() {
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Link link = createLink(URI.create(URL));
        TelegramChatLink binding = createBinding(chatId, link.getId());
        createRule(ruleId);
        TelegramChatLinkRule bindingRule = createBindingRule(binding.getId(), ruleId, Map.of("amount", "513"));

        ruleService.removeRule(chatId, link.getId(), ruleId);

        Optional<TelegramChatLinkRule> optionalBindingRule = bindingRuleRepository.findById(bindingRule.getId());
        assertFalse(optionalBindingRule.isPresent());

        List<TelegramChatLinkRuleOutbox> optionalOutbox =
                bindingRuleOutboxRepository.findAllByTelegramChatLinkRuleId(bindingRule.getId());
        assertEquals(1, optionalOutbox.size());
        assertEquals(ProcessType.PENDING, optionalOutbox.getFirst().getProcessType());
        assertEquals(OutboxEventType.REMOVE, optionalOutbox.getFirst().getEventType());
    }

    @Test
    @DisplayName("Выбрасывается исключение при несуществующем чате")
    public void removeRule_shouldThrow_whenChatNotFound() {
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        Link link = createLink(URI.create(URL));
        createRule(ruleId);

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.removeRule(chatId, link.getId(), ruleId));
    }

    @Test
    @DisplayName("Выбрасывается исключение при несуществующей ссылке")
    public void removeRule_shouldThrow_whenLinkNotFound() {
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        createRule(ruleId);
        Long linkId = RandomUtils.nextLong();

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.removeRule(chatId, linkId, ruleId));
    }

    @Test
    @DisplayName("Выбрасывается исключение при неотслеживаемой ссылке")
    public void removeRule_shouldThrow_whenLinkNotAdded() {
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Link link = createLink(URI.create(URL));
        createRule(ruleId);

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.removeRule(chatId, link.getId(), ruleId));
    }

    @Test
    @DisplayName("Выбрасывается исключение при несуществующем правиле")
    public void removeRule_shouldThrow_whenRuleNotFound() {
        Long chatId = RandomUtils.nextLong();
        createTelegramChat(chatId);
        Link link = createLink(URI.create(URL));
        createBinding(chatId, link.getId());
        Long ruleId = RandomUtils.nextLong();

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.removeRule(chatId, link.getId(), ruleId));
    }

    @Test
    @DisplayName("Выбрасывается исключение при неотслеживаемом правиле")
    public void removeRule_shouldThrow_whenRuleNotAdded() {
        Long chatId = RandomUtils.nextLong();
        Long ruleId = 1L;
        createTelegramChat(chatId);
        Link link = createLink(URI.create(URL));
        createBinding(chatId, link.getId());
        createRule(ruleId);

        assertThrows(UnprocessableEntityException.class,
                () -> ruleService.removeRule(chatId, link.getId(), ruleId));
    }
}
