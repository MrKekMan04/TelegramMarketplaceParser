package ru.overcode.gateway.model.chatlink;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Связка ссылки и телеграм чата пользователя
 */
@Entity
@Getter
@Setter
@Table(name = "telegram_chat_link")
public class TelegramChatLink {

    /**
     * Идентификатор связки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор телеграм чата
     */
    private Long chatId;

    /**
     * Идентификатор ссылки
     */
    private Long linkId;
}
