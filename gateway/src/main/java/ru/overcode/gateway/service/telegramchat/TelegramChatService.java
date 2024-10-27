package ru.overcode.gateway.service.telegramchat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;

@Service
@RequiredArgsConstructor
public class TelegramChatService {

    private final TelegramChatDbService telegramChatDbService;

    public void throwIfNotExists(Long chatId) {
        telegramChatDbService.findById(chatId)
                .orElseThrow(() -> new UnprocessableEntityException(GatewayExceptionMessage.CHAT_NOT_FOUND
                        .withParam("chatId", chatId.toString())));
    }
}
