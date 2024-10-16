package ru.overcode.gateway.mapper;

import org.mapstruct.Mapper;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.dto.chatlink.rule.LinkRuleDto;
import ru.overcode.gateway.dto.link.RuleDto;

@Mapper(config = MappersConfig.class)
public interface RuleMapper {

    RuleDto toRuleDto(LinkRuleDto dto);
}
