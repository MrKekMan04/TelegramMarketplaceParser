package ru.overcode.gateway.repository.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.dto.chatlink.rule.LinkRuleDto;
import ru.overcode.gateway.model.rule.Rule;

import java.util.Collection;
import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    @Query("""
            select
                tcl.linkId as linkId,
                r.id as ruleId,
                r.description as ruleDescription
            from TelegramChatLink tcl
            join TelegramChatLinkRule tclr on tcl.id = tclr.chatLinkId
            join Rule r on r.id = tclr.ruleId
            where tcl.chatId = :chatId and tcl.linkId in :linkIds
            """)
    List<LinkRuleDto> findAllByChatIdAndLinkIdIn(Long chatId, Collection<Long> linkIds);
}
