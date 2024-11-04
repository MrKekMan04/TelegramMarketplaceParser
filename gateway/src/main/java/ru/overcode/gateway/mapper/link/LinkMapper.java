package ru.overcode.gateway.mapper.link;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.dto.link.AddLinkResponse;
import ru.overcode.gateway.dto.link.GetLinkResponse;
import ru.overcode.gateway.dto.rule.RuleDto;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.model.link.LinkOutbox;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.dto.market.MarketName;
import ru.overcode.shared.stream.LinkOutboxDto;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper(config = MappersConfig.class)
public interface LinkMapper {

    @Mapping(target = "id", ignore = true)
    Link toLink(URI url, Long marketId);

    AddLinkResponse toAddLinkResponse(Long linkId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "linkId", source = "link.id")
    @Mapping(target = "linkUrl", source = "link.url")
    @Mapping(target = "processType", ignore = true)
    LinkOutbox toOutbox(Link link, MarketName marketName, OutboxEventType eventType);

    @Mapping(target = "id", source = "linkId")
    @Mapping(target = "url", source = "linkUrl")
    LinkOutboxDto toOutboxDto(LinkOutbox outbox);

    default List<GetLinkResponse> toGetLinkResponse(
            Map<Long, Link> linksById,
            Map<Long, List<RuleDto>> linkRuleDtoByLinkId
    ) {
        return linksById.keySet().stream()
                .map(linkId -> toGetLinkResponse(
                        linksById.get(linkId),
                        Optional.ofNullable(linkRuleDtoByLinkId.get(linkId)).orElse(List.of())))
                .toList();
    }

    default GetLinkResponse toGetLinkResponse(Link link, List<RuleDto> linkRules) {
        return new GetLinkResponse(link.getId(), link.getUrl(), linkRules);
    }
}
