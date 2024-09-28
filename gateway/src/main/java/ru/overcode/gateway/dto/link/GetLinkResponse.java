package ru.overcode.gateway.dto.link;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

public record GetLinkResponse(
        @Schema(description = "Внутренний идентификатор ссылки", requiredMode = Schema.RequiredMode.REQUIRED)
        Long linkId,
        @Schema(description = "Отслеживаемая ссылка", requiredMode = Schema.RequiredMode.REQUIRED)
        URI linkUrl,
        @Schema(description = "Список отслеживаемых правил для ссылки", requiredMode = Schema.RequiredMode.REQUIRED)
        List<RuleDto> rules
) {

}
