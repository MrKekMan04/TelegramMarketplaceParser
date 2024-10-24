package ru.overcode.gateway.service.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.mapper.telegramchat.TelegramChatMapper;
import ru.overcode.gateway.service.telegramchat.TelegramChatDbService;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final TelegramChatDbService telegramChatDbService;
    private final TelegramChatMapper telegramChatMapper;

    @Transactional
    public void registerChat(Long chatId) {
        telegramChatDbService.findById(chatId)
                .ifPresent(ignore -> {
                    throw new UnprocessableEntityException(GatewayExceptionMessage.CHAT_ALREADY_REGISTERED);
                });

        telegramChatDbService.save(telegramChatMapper.toTelegramChat(chatId));
    }
}
