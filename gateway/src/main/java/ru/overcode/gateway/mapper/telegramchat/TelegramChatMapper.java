package ru.overcode.gateway.mapper.telegramchat;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.model.telegramchat.TelegramChat;

@Mapper(config = MappersConfig.class)
public interface TelegramChatMapper {

    @Mapping(target = "id", source = "chatId")
    TelegramChat toTelegramChat(Long chatId);
}
