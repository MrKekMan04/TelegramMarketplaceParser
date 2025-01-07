package ru.overcode.bot.dto.link;

import io.swagger.v3.oas.annotations.media.Schema;

public record RemoveLinkRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        Long chatId
) {
}
