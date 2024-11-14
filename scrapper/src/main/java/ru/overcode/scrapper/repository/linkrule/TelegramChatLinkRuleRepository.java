package ru.overcode.scrapper.repository.linkrule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;

@Repository
public interface TelegramChatLinkRuleRepository extends JpaRepository<TelegramChatLinkRule, Long> {
}
