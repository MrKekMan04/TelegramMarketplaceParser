package ru.overcode.gateway.repository.telegramchat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.telegramchat.TelegramChat;

@Repository
public interface TelegramChatRepository extends JpaRepository<TelegramChat, Long> {

}
