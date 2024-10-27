package ru.overcode.gateway.mapper.chatlink;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;

@Mapper(config = MappersConfig.class)
public interface TelegramChatLinkMapper {

    @Mapping(target = "id", ignore = true)
    TelegramChatLink toTelegramChatLink(Long chatId, Long linkId);
}
