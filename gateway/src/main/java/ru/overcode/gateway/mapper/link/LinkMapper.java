package ru.overcode.gateway.mapper.link;

import org.mapstruct.Mapper;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.dto.link.AddLinkResponse;
import ru.overcode.gateway.dto.link.GetLinkResponse;
import ru.overcode.gateway.dto.link.RuleDto;
import ru.overcode.gateway.model.link.Link;

import java.util.List;
import java.util.Map;

@Mapper(config = MappersConfig.class)
public interface LinkMapper {

    AddLinkResponse toAddLinkResponse(Long linkId);

    default List<GetLinkResponse> toGetLinkResponse(
            Map<Long, Link> linksById,
            Map<Long, List<RuleDto>> linkRuleDtoByLinkId
    ) {
        return linksById.keySet().stream()
                .map(linkId -> toGetLinkResponse(linksById.get(linkId), linkRuleDtoByLinkId.get(linkId)))
                .toList();
    }

    default GetLinkResponse toGetLinkResponse(Link link, List<RuleDto> linkRules) {
        return new GetLinkResponse(link.getId(), link.getUrl(), linkRules);
    }
}
