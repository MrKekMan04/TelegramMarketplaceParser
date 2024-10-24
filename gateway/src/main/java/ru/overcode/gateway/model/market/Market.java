package ru.overcode.gateway.model.market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.overcode.gateway.dto.market.MarketName;

/**
 * Маркетплейс
 */
@Entity
@Getter
@Setter
@Table(name = "market")
public class Market {

    /**
     * Идентификатор маркетплейса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название маркетплейса
     *
     * @see MarketName
     */
    @Enumerated(EnumType.STRING)
    private MarketName name;

    /**
     * Доменное имя маркетплейса
     */
    private String urlDomain;
}
