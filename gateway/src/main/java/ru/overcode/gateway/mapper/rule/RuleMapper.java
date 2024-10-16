package ru.overcode.gateway.mapper.rule;

import org.mapstruct.Mapper;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.dto.chatlink.rule.LinkRuleDto;
import ru.overcode.gateway.dto.rule.RuleDto;

@Mapper(config = MappersConfig.class)
public interface RuleMapper {

    RuleDto toRuleDto(LinkRuleDto dto);
}
