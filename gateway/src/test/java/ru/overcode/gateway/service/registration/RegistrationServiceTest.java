package ru.overcode.gateway.service.registration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.model.telegramchat.TelegramChat;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationServiceTest extends BaseIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Test
    @DisplayName("Регистрация нвоого чата проходит успешно")
    public void registerChat_shouldRegister_whenChatNotExists() {
        Long chatId = RandomUtils.nextLong();

        registrationService.registerChat(chatId);

        Optional<TelegramChat> optionalChat = telegramChatRepository.findById(chatId);

        assertTrue(optionalChat.isPresent());
    }

    @Test
    @DisplayName("Происходит исключение при попытке зарегистрировать повторно чат")
    public void registerChat_shouldThrow_whenChatExists() {
        Long chatId = RandomUtils.nextLong();

        createTelegramChat(chatId);

        assertThrows(UnprocessableEntityException.class, () -> registrationService.registerChat(chatId));
    }
}
