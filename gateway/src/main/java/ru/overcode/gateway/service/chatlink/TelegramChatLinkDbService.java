package ru.overcode.gateway.service.chatlink;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.repository.chatlink.TelegramChatLinkRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkDbService {

    private final TelegramChatLinkRepository telegramChatLinkRepository;

    @Transactional(readOnly = true)
    public Optional<TelegramChatLink> findByChatIdAndLinkId(Long chatId, Long linkId) {
        return telegramChatLinkRepository.findByChatIdAndLinkId(chatId, linkId);
    }

    @Transactional
    public void save(TelegramChatLink telegramChatLink) {
        telegramChatLinkRepository.save(telegramChatLink);
    }

    @Transactional
    public void deleteById(Long id) {
        telegramChatLinkRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<TelegramChatLink> findById(Long bindingId) {
        return telegramChatLinkRepository.findById(bindingId);
    }
}
