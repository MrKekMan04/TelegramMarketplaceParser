package ru.overcode.gateway.model.link;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.event.ProcessType;
import ru.overcode.shared.dto.market.MarketName;

import java.net.URI;

/**
 * Изменения ссылок
 */
@Entity
@Getter
@Setter
@Table(name = "link_outbox")
public class LinkOutbox {

    /**
     * Идентификатор изменения
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор ссылки
     */
    private Long linkId;

    /**
     * Адрес ссылки
     */
    private URI linkUrl;

    /**
     * Наименование маркетплейса
     */
    @Enumerated(EnumType.STRING)
    private MarketName marketName;

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
