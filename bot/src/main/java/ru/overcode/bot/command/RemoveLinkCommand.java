package ru.overcode.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoveLinkCommand implements Command {

    private static final String COMMAND_NAME = "/remove-link";
    private static final String COMMAND_DESCRIPTION = "Удалить ссылку из отслеживаемых";

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

        return new SendMessage(chat.id(), getResponseMessage(chat))
                .parseMode(ParseMode.HTML);
    }

    private String getResponseMessage(Chat chat) {
        return "Ok";
    }
}