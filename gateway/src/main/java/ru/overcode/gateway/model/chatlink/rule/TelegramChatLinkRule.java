package ru.overcode.gateway.model.chatlink.rule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.overcode.gateway.config.converter.RuleParamsConverter;

import java.util.Map;

/**
 * Связка телеграм чата пользователя с правилом маркетплейса
 */
@Entity
@Getter
@Setter
@Table(name = "telegram_chat_link_rule")
public class TelegramChatLinkRule {

    /**
     * Идентификатор связки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор связки телеграм чата с ссылкой
     */
    private Long chatLinkId;

    /**
     * Идентификатор правила
     */
    private Long ruleId;

    /**
     * Параметры правила
     */
    @Convert(converter = RuleParamsConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> params;
}
