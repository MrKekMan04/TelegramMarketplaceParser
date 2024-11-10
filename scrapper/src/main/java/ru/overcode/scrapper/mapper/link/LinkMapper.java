package ru.overcode.scrapper.mapper.link;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.overcode.scrapper.config.mapper.MappersConfig;
import ru.overcode.scrapper.model.link.Link;
import ru.overcode.shared.stream.LinkOutboxDto;

@Mapper(config = MappersConfig.class)
public interface LinkMapper {

    void fillLink(@MappingTarget Link link, LinkOutboxDto dto);
}