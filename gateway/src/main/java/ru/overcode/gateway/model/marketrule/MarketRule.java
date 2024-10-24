package ru.overcode.gateway.model.marketrule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Связка маркетплейса с правилом
 */
@Entity
@Getter
@Setter
@Table(name = "market_rule")
public class MarketRule {

    /**
     * Идентификатор связки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор маркетплейса
     */
    private Long marketId;

    /**
     * Идентификатор правила
     */
    private Long ruleId;
}
