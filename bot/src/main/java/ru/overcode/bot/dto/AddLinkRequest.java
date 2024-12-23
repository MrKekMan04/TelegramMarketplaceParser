package ru.overcode.bot.dto;

import java.net.URI;

public record AddLinkRequest(Long chatId, URI linkUrl) {
}
