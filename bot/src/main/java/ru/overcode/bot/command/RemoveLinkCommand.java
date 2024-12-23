package ru.overcode.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.overcode.bot.config.feign.link.LinkFeignClient;
import ru.overcode.bot.dto.link.RemoveLinkRequest;

@Component
@RequiredArgsConstructor
public class RemoveLinkCommand implements Command {

    private static final String COMMAND_NAME = "/remove-link";
    private static final String COMMAND_DESCRIPTION = "Удалить ссылку из отслеживаемых";

    private final LinkFeignClient linkFeignClient;

    @Override
    public String command() {
        return COMMAND_NAME;
    }

    @Override
    public String description() {
        return COMMAND_DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        Chat chat = update.message().chat();
        Long chatId = update.message().chat().id();
        var response = linkFeignClient.removeLink(1L, new RemoveLinkRequest(chatId));
        return new SendMessage(chat.id(), response.toString());
    }
}