package ru.overcode.gateway.repository.chatlink.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRuleOutbox;
import ru.overcode.shared.dto.event.ProcessType;

import java.util.List;

@Repository
public interface TelegramChatLinkRuleOutboxRepository extends JpaRepository<TelegramChatLinkRuleOutbox, Long> {

    List<TelegramChatLinkRuleOutbox> findAllByProcessType(ProcessType processType);

    @Modifying
    @Query("""
            update TelegramChatLinkRuleOutbox o
            set o.processType = :processType
            where o.id = :id
            """)
    void setProcessType(Long id, ProcessType processType);
}
