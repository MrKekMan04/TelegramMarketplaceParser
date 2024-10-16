package ru.overcode.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.overcode.gateway.model.telegramchat.TelegramChat;
import ru.overcode.gateway.repository.telegramchat.TelegramChatRepository;

@SpringBootTest
@Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseIntegrationTest {

    @Autowired
    protected TelegramChatRepository telegramChatRepository;

    protected void createTelegramChat(Long chatId) {
        telegramChatRepository.save(new TelegramChat()
                .setId(chatId));
    }
}
