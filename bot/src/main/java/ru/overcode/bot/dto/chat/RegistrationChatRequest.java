package ru.overcode.bot.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegistrationChatRequest(
        @Schema(description = "Идентификатор телеграмм чата", requiredMode = Schema.RequiredMode.REQUIRED)
        Long chatId
) {
}
