package ru.overcode.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.overcode.bot.service.CommandService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddLinkCommand implements Command {

    private static final String COMMAND_NAME = "/add-link";
    private static final String COMMAND_DESCRIPTION = "Добавить ссылку в отслеживаемые";

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
        String response = commandService.addLink(chatId, parameters);
        return new SendMessage(chatId, response);
    }
}