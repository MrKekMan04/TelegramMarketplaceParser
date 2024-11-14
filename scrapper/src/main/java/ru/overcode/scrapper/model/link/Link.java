package ru.overcode.scrapper.model.link;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.overcode.scrapper.config.converter.UriConverter;
import ru.overcode.shared.dto.market.MarketName;

import java.net.URI;

@Entity
@Getter
@Setter
@Table(name = "link")
public class Link {

    @Id
    private Long id;

    @Convert(converter = UriConverter.class)
    private URI url;

    @Enumerated(EnumType.STRING)
    private MarketName marketName;
}
