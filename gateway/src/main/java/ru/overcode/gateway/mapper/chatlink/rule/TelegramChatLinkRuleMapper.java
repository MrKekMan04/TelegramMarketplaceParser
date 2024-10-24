package ru.overcode.gateway.mapper.chatlink.rule;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;

import java.util.Map;

@Mapper(config = MappersConfig.class)
public interface TelegramChatLinkRuleMapper {

    @Mapping(target = "id", ignore = true)
    TelegramChatLinkRule toBindingRule(Long chatLinkId, Long ruleId, Map<String, String> params);
}
