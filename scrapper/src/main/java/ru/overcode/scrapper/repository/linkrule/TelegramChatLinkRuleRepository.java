package ru.overcode.scrapper.repository.linkrule;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;

public interface TelegramChatLinkRuleRepository extends JpaRepository<TelegramChatLinkRule, Long> {
}
