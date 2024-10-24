package ru.overcode.gateway.model.telegramchat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Телеграм чат пользователя
 */
@Entity
@Getter
@Setter
@Table(name = "telegram_chat")
public class TelegramChat {

    /**
     * Идентификатор телеграмм чата
     */
    @Id
    private Long id;
}
