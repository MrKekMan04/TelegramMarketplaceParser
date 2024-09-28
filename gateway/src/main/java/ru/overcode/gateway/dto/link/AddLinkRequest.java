package ru.overcode.gateway.dto.link;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.net.URI;

public record AddLinkRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`chatId` не может быть пустым")
        @Positive(message = "`chatId` не может быть отрицательным")
        Long chatId,
        @Schema(description = "Желаемая отслеживаемая ссылка", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`linkUrl` не может быть пустым")
        URI linkUrl
) {
}
