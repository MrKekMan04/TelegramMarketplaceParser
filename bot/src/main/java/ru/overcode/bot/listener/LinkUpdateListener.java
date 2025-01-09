package ru.overcode.bot.listener;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.overcode.bot.bot.Bot;
import ru.overcode.shared.stream.update.GatewayLinkUpdateDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdateListener {

    private final Bot bot;

    @KafkaListener(
            containerFactory = "linkUpdateContainerFactory",
            topics = "${kafka.consumers.link-update.topic}",
            groupId = "${kafka.consumers.link-update.group-id}",
            autoStartup = "${kafka.consumers.link-update.enable}"
    )
    public void listenLinkUpdates(GatewayLinkUpdateDto linkUpdate) {
        log.info("Received link update: {}", linkUpdate);

        String message = String.format(
                "По [ссылке](%s) с id %d произошло обновление:\n%s",
                linkUpdate.linkUrl(),
                linkUpdate.linkId(),
                linkUpdate.updateMessage()
        );

        bot.execute(new SendMessage(linkUpdate.chatId(), message)
                .parseMode(ParseMode.Markdown));
    }
}

