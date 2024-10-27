package ru.overcode.gateway.repository.chatlink.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;

import java.util.Optional;

@Repository
public interface TelegramChatLinkRuleRepository extends JpaRepository<TelegramChatLinkRule, Long> {

    @Modifying
    @Query("""
            delete from TelegramChatLinkRule tclr
            where tclr.chatLinkId = :chatLinkId
            """)
    void deleteAllByChatLinkId(Long chatLinkId);

    Optional<TelegramChatLinkRule> findByChatLinkIdAndRuleId(Long chatLinkId, Long ruleId);

    @Modifying
    @Query("""
            delete from TelegramChatLinkRule tclr
            where tclr.id = :id
            """)
    void deleteById(@NonNull Long id);
}
