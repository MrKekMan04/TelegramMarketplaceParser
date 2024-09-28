package ru.overcode.gateway.dto.link;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddLinkResponse(
        @Schema(description = "Внутренний идентификатор ссылки", requiredMode = Schema.RequiredMode.REQUIRED)
        Long linkId
) {
}
