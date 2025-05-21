package ru.overcode.gateway.mapper.marketrule;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.model.marketrule.MarketRule;

@Mapper(config = MappersConfig.class)
public interface MarketRuleMapper {

    @Mapping(target = "id", ignore = true)
    MarketRule toEntity(Long marketId, Long ruleId);
}
