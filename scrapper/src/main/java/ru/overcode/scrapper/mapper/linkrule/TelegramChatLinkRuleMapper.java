package ru.overcode.scrapper.mapper.linkrule;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.overcode.scrapper.config.mapper.MappersConfig;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

@Mapper(config = MappersConfig.class)
public interface TelegramChatLinkRuleMapper {

    @Mapping(target = "id", ignore = true)
    void fillTelegramChatLinkRule(@MappingTarget TelegramChatLinkRule telegramChatLinkRule, LinkRuleOutboxDto dto);
}
