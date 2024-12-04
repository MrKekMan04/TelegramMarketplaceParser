package ru.overcode.scrapper.repository.linkrule;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;

@Repository
public interface TelegramChatLinkRuleRepository extends JpaRepository<TelegramChatLinkRule, Long> {

    Slice<TelegramChatLinkRule> findTelegramChatLinkRulesByLinkId(Long linkId, Pageable pageable);
}
