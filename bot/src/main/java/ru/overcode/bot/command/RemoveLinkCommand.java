package ru.overcode.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.overcode.bot.service.CommandService;

@Component
@RequiredArgsConstructor
public class RemoveLinkCommand implements Command {

    private static final String COMMAND_NAME = "/remove-link";
    private static final String COMMAND_DESCRIPTION = "Удалить ссылку из отслеживаемых";

    private final CommandService commandService;

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
        Long chatId = update.message().chat().id();
        String[] parameters = update.message().text().split(" ");
        String response = commandService.removeLink(chatId, parameters);
        return new SendMessage(chatId, response)
                .parseMode(ParseMode.Markdown);
    }
}