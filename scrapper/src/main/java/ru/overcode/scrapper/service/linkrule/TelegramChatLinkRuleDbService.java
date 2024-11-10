package ru.overcode.scrapper.service.linkrule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;
import ru.overcode.scrapper.repository.linkrule.TelegramChatLinkRuleRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkRuleDbService {

    private final TelegramChatLinkRuleRepository telegramChatLinkRuleRepository;

    @Transactional
    public void saveAll(List<TelegramChatLinkRule> telegramChatLinkRules) {
        telegramChatLinkRuleRepository.saveAll(telegramChatLinkRules);
    }

    @Transactional
    public void deleteAllByIdInBatch(Collection<Long> telegramChatLinkRuleIds) {
        telegramChatLinkRuleRepository.deleteAllByIdInBatch(telegramChatLinkRuleIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, TelegramChatLinkRule> findAllById(Collection<Long> telegramChatLinkRuleIds) {
        return telegramChatLinkRuleRepository.findAllById(telegramChatLinkRuleIds).stream()
                .collect(Collectors.toMap(
                        TelegramChatLinkRule::getId,
                        Function.identity()
                ));
    }
}
