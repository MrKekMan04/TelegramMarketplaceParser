package ru.overcode.shared.stream.update;

import java.net.URI;

public record GatewayLinkUpdateDto(
        Long chatId,
        Long linkId,
        URI linkUrl,
        String updateMessage
) {

}
