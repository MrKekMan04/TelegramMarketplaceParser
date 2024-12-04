package ru.overcode.scrapper.consumer.linkrule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.scrapper.BaseIntegrationTest;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TelegramChatLinkRuleConsumerTest extends BaseIntegrationTest {

    @Autowired
    private TelegramChatLinkRuleConsumer telegramChatLinkRuleConsumer;

    @Test
    @DisplayName("Правило для ссылки сохраняется в таблицу telegram_chat_link_rule")
    public void handleLinkRuleOutboxDto_shouldSave_whenLinkRuleDoesNotExist() {
        List<LinkRuleOutboxDto> linkRuleOutboxDtos = List.of(
                new LinkRuleOutboxDto(
                        RandomUtils.nextLong(),
                        1L,
                        2L,
                        Map.of("key", "value"),
                        OutboxEventType.UPSERT
                )
        );

        telegramChatLinkRuleConsumer.handleLinkRuleOutboxDto(linkRuleOutboxDtos);

        Optional<TelegramChatLinkRule> linkRule = telegramChatLinkRuleRepository.findById(linkRuleOutboxDtos.getFirst().id());

        assertTrue(linkRule.isPresent());
        assertEquals(linkRuleOutboxDtos.getFirst().linkId(), linkRule.get().getLinkId());
        assertEquals(linkRuleOutboxDtos.getFirst().ruleId(), linkRule.get().getRuleId());

        Map<String, String> expectedParams = linkRuleOutboxDtos.getFirst().params();
        Map<String, String> actualParams = linkRule.get().getParams();

        assertEquals(expectedParams.size(), actualParams.size());

        expectedParams.forEach((key, value) ->
                assertEquals(value, actualParams.get(key))
        );
    }

    @Test
    @DisplayName("Правило для ссылки обновляется в таблице telegram_chat_link_rule")
    public void handleLinkRuleOutboxDto_shouldUpdate_whenLinkRuleExists() {
        Long linkRuleId = RandomUtils.nextLong();
        createLinkRule(linkRuleId, 3L, 4L, Map.of("test", "answer"));

        List<LinkRuleOutboxDto> linkRuleOutboxDtos = List.of(
                new LinkRuleOutboxDto(
                        linkRuleId,
                        1L,
                        2L,
                        Map.of("key", "value"),
                        OutboxEventType.UPSERT
                )
        );

        telegramChatLinkRuleConsumer.handleLinkRuleOutboxDto(linkRuleOutboxDtos);

        Optional<TelegramChatLinkRule> linkRule = telegramChatLinkRuleRepository.findById(linkRuleOutboxDtos.getFirst().id());

        assertTrue(linkRule.isPresent());
        assertEquals(linkRuleOutboxDtos.getFirst().linkId(), linkRule.get().getLinkId());
        assertEquals(linkRuleOutboxDtos.getFirst().ruleId(), linkRule.get().getRuleId());

        Map<String, String> expectedParams = linkRuleOutboxDtos.getFirst().params();
        Map<String, String> actualParams = linkRule.get().getParams();

        assertEquals(expectedParams.size(), actualParams.size());

        expectedParams.forEach((key, value) ->
                assertEquals(value, actualParams.get(key))
        );
    }

    @Test
    @DisplayName("Правило для ссылки удаляется в таблице telegram_chat_link_rule")
    public void handleLinkRuleOutboxDto_shouldDelete_whenLinkRuleExists() {
        Long linkRuleId = RandomUtils.nextLong();
        createLinkRule(linkRuleId, 3L, 4L, Map.of("test", "answer"));

        List<LinkRuleOutboxDto> linkRuleOutboxDtos = List.of(
                new LinkRuleOutboxDto(
                        linkRuleId,
                        null,
                        null,
                        null,
                        OutboxEventType.REMOVE
                )
        );

        telegramChatLinkRuleConsumer.handleLinkRuleOutboxDto(linkRuleOutboxDtos);

        Optional<TelegramChatLinkRule> linkRule = telegramChatLinkRuleRepository.findById(linkRuleOutboxDtos.getFirst().id());

        assertFalse(linkRule.isPresent());
    }
}