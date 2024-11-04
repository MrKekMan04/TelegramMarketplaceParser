package ru.overcode.gateway.model.chatlink.rule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.overcode.gateway.config.converter.RuleParamsConverter;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.event.ProcessType;

import java.util.Map;

/**
 * Изменения правил отслеживания
 */
@Entity
@Getter
@Setter
@Table(name = "telegram_chat_link_rule_outbox")
public class TelegramChatLinkRuleOutbox {

    /**
     * Идентификатор изменения
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор связки с правилом
     */
    private Long telegramChatLinkRuleId;

    /**
     * Идентификатор ссылки
     */
    private Long linkId;

    /**
     * Идентификатор правила
     */
    private Long ruleId;

    /**
     * Параметры отслеживания
     */
    @Convert(converter = RuleParamsConverter.class)
    private Map<String, String> params;

    /**
     * Тип события
     */
    @Enumerated(EnumType.STRING)
    private OutboxEventType eventType;

    /**
     * Тип обработки события
     */
    @Enumerated(EnumType.STRING)
    private ProcessType processType = ProcessType.PENDING;
}


