package ru.overcode.gateway.service.telegramchat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.telegramchat.TelegramChat;
import ru.overcode.gateway.repository.telegramchat.TelegramChatRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramChatDbService {

    private final TelegramChatRepository telegramChatRepository;

    @Transactional(readOnly = true)
    public Optional<TelegramChat> findById(Long id) {
        return telegramChatRepository.findById(id);
    }

    @Transactional
    public void save(TelegramChat telegramChat) {
        telegramChatRepository.save(telegramChat);
    }
}
