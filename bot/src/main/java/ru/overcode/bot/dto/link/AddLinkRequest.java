package ru.overcode.bot.dto.link;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;

public record AddLinkRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        Long chatId,
        @Schema(description = "Желаемая отслеживаемая ссылка", requiredMode = Schema.RequiredMode.REQUIRED)
        URI linkUrl
) {
}