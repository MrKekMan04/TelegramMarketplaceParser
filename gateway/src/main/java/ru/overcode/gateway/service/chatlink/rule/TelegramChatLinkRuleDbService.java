package ru.overcode.gateway.service.chatlink.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.repository.chatlink.rule.TelegramChatLinkRuleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkRuleDbService {

    private final TelegramChatLinkRuleRepository telegramChatLinkRuleRepository;

    @Transactional
    public void deleteAllByChatLinkId(Long chatLinkId) {
        telegramChatLinkRuleRepository.deleteAllByChatLinkId(chatLinkId);
    }

    @Transactional
    public TelegramChatLinkRule save(TelegramChatLinkRule telegramChatLinkRule) {
        return telegramChatLinkRuleRepository.save(telegramChatLinkRule);
    }

    @Transactional(readOnly = true)
    public Optional<TelegramChatLinkRule> findByBindingIdAndRuleId(Long bindingId, Long ruleId) {
        return telegramChatLinkRuleRepository.findByChatLinkIdAndRuleId(bindingId, ruleId);
    }

    @Transactional
    public void deleteById(Long bindingRuleId) {
        telegramChatLinkRuleRepository.deleteById(bindingRuleId);
    }

    @Transactional(readOnly = true)
    public Optional<TelegramChatLinkRule> findById(Long bindingRuleId) {
        return telegramChatLinkRuleRepository.findById(bindingRuleId);
    }
}
