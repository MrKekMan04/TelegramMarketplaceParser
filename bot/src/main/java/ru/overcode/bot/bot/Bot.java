package ru.overcode.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.overcode.bot.command.Command;
import ru.overcode.bot.config.ApplicationConfig;

import java.util.List;

@Component
@Slf4j
public class Bot extends TelegramBot {
    private final List<Command> commands;

    public Bot(ApplicationConfig config, List<Command> commands) {//
        super(config.telegramToken());

        this.commands = commands;
        this.setUpMenuCommands();
        this.setUpdatesListener(this::onTelegramUpdateReceived);
    }

    private void setUpMenuCommands() {
        execute(new SetMyCommands(commands.stream()
                .map(Command::toApiCommand)
                .toArray(BotCommand[]::new)));
    }

    private int onTelegramUpdateReceived(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null) {
                Long chatId = update.message().chat().id();
                String message = update.message().text();

                if (message.startsWith("/")) {
                    processCommandMessage(update, chatId, message);
                } else {
                    processNonCommandMessage(chatId, message);
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processCommandMessage(Update update, Long chatId, String message) {
        log.info("[Chat id: {}] process command: {}", chatId, message);

        SendMessage responseMessage = commands.stream()
                .filter(command -> isCommandMatched(message, command.command()))
                .findFirst()
                .map(command -> command.handle(update))
                .orElse(new SendMessage(chatId, "Неизвестная команда. Доступные команды: /help"));

        execute(responseMessage);
    }

    private boolean isCommandMatched(String message, String command) {
        return message.startsWith(command)
                && (message.length() == command.length() || message.charAt(command.length()) == ' ');
    }

    private void processNonCommandMessage(Long chatId, String message) {
        log.info("[Chat id: {}] process text: {}", chatId, message);
        execute(new SendMessage(chatId, "Введите команду. Доступные команды: /help"));
    }
}