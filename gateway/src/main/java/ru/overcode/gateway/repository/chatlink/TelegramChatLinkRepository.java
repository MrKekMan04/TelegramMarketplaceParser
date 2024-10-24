package ru.overcode.gateway.repository.chatlink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;

import java.util.Optional;

@Repository
public interface TelegramChatLinkRepository extends JpaRepository<TelegramChatLink, Long> {

    Optional<TelegramChatLink> findByChatIdAndLinkId(Long chatId, Long linkId);

    @Modifying
    @Query("""
        delete from TelegramChatLink tcl
        where tcl.id = :id
        """)
    void deleteById(@NonNull Long id);
}
