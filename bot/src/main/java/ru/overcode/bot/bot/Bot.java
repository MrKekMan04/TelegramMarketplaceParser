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

    public Bot(ApplicationConfig config, List<Command> commands) {
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
        updates.stream()
                .filter(update -> update.message() != null)
                .forEach(update -> {
                    Long chatId = update.message().chat().id();
                    String message = update.message().text();

                    SendMessage responseMessage = message.startsWith("/")
                            ? processCommandMessage(update, chatId, message)
                            : processNonCommandMessage(chatId, message);

                    execute(responseMessage);
                });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private SendMessage processCommandMessage(Update update, Long chatId, String message) {
        log.info("[Chat id: {}] process command: {}", chatId, message);

        return commands.stream()
                .filter(command -> isCommandMatched(message, command.command()))
                .findFirst()
                .map(command -> command.handle(update))
                .orElse(new SendMessage(chatId, "Неизвестная команда. Доступные команды: /help"));
    }

    private boolean isCommandMatched(String message, String command) {
        return message.startsWith(command)
                && (message.length() == command.length() || message.charAt(command.length()) == ' ');
    }

    private SendMessage processNonCommandMessage(Long chatId, String message) {
        log.info("[Chat id: {}] process text: {}", chatId, message);
        return new SendMessage(chatId, "Введите команду. Доступные команды: /add-link, /add-rule, /get-links, /get-rules, /remove-rule, /remove-link");
    }
}