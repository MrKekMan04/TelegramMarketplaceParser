package ru.overcode.gateway.service.chatlink.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.repository.chatlink.rule.TelegramChatLinkRuleRepository;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkRuleDbService {

    private final TelegramChatLinkRuleRepository telegramChatLinkRuleRepository;

    @Transactional
    public void deleteAllByChatLinkId(Long chatLinkId) {
        telegramChatLinkRuleRepository.deleteAllByChatLinkId(chatLinkId);
    }
}
