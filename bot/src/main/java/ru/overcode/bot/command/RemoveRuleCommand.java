package ru.overcode.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.overcode.bot.service.CommandService;

@Component
@RequiredArgsConstructor
public class RemoveRuleCommand implements Command {

    private static final String COMMAND_NAME = "/remove-rule";
    private static final String COMMAND_DESCRIPTION = "Отвязать правило отслеживания для ссылки";

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
        String response = commandService.removeRule(chatId, parameters);
        return new SendMessage(chatId, response)
                .parseMode(ParseMode.Markdown);
    }
}