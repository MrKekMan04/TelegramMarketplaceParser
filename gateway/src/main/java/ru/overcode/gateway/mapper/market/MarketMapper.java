package ru.overcode.gateway.mapper.market;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.dto.market.CreateMarketRequest;
import ru.overcode.gateway.dto.market.InternalMarketDto;
import ru.overcode.gateway.dto.market.MarketIdDto;
import ru.overcode.gateway.dto.market.UpdateMarketRequest;
import ru.overcode.gateway.model.market.Market;

import java.util.List;

@Mapper(config = MappersConfig.class)
public interface MarketMapper {

    MarketIdDto toMarketIdDto(Market market);

    InternalMarketDto toInternalMarketDto(Market market);

    default List<InternalMarketDto> toInternalMarketDto(List<Market> markets) {
        return markets.stream()
                .map(this::toInternalMarketDto)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    Market toEntity(CreateMarketRequest request);

    @Mapping(target = "id", ignore = true)
    void fillEntity(@MappingTarget Market market, UpdateMarketRequest request);
}
