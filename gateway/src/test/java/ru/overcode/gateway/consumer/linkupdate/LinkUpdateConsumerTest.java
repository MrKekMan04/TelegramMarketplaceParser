package ru.overcode.gateway.consumer.linkupdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.producer.linkupdate.LinkUpdateProducer;
import ru.overcode.shared.stream.update.GatewayLinkUpdateDto;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

import java.net.URI;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class LinkUpdateConsumerTest extends BaseIntegrationTest {

    @Autowired
    private LinkUpdateConsumer linkUpdateConsumer;

    @MockBean
    private LinkUpdateProducer linkUpdateProducer;

    @BeforeEach
    public void resetMocks() {
        reset(linkUpdateProducer);
    }

    @Test
    @DisplayName("Данные по обновлению обогащаются и переотправляются в кафку")
    public void consume_shouldFillDataAndSendToKafka_whenGetUpdate() {
        long chatId = RandomUtils.nextLong();
        String amount = "132";
        createTelegramChat(chatId);
        Link link = createLink(URI.create("https://google.com"));
        TelegramChatLink binding = createBinding(chatId, link.getId());
        createRule(1L, "%s");
        TelegramChatLinkRule bindingRule = createBindingRule(binding.getId(), 1L, Map.of("amount", amount));

        ScrapperLinkUpdateDto record = new ScrapperLinkUpdateDto(bindingRule.getId());

        linkUpdateConsumer.consume(record);

        verify(linkUpdateProducer).send(new GatewayLinkUpdateDto(
                chatId,
                link.getId(),
                link.getUrl(),
                amount
        ));
    }
}
