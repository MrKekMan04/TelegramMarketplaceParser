package ru.overcode.gateway.model.link;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.overcode.gateway.config.converter.UriConverter;

import java.net.URI;

/**
 * Ссылка
 */
@Entity
@Getter
@Setter
@Table(name = "link")
public class Link {

    /**
     * Идентификатор ссылки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Адрес ссылки
     */
    @Convert(converter = UriConverter.class)
    private URI url;

    /**
     * Идентификатор маркетплейса
     */
    private Long marketId;
}
