package ru.overcode.gateway.dto.chatlink;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;

@Mapper(componentModel = "spring")
public interface TelegramChatLinkMapper {

    @Mapping(target = "id", ignore = true)
    TelegramChatLink toTelegramChatLink(Long chatId, Long linkId);
}
