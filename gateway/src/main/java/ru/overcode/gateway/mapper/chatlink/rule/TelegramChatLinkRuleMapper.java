package ru.overcode.gateway.mapper.chatlink.rule;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRuleOutbox;
import ru.overcode.shared.dto.event.OutboxEventType;
import ru.overcode.shared.stream.LinkRuleOutboxDto;

import java.util.Map;

@Mapper(config = MappersConfig.class)
public interface TelegramChatLinkRuleMapper {

    @Mapping(target = "id", ignore = true)
    TelegramChatLinkRule toBindingRule(Long chatLinkId, Long ruleId, Map<String, String> params);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramChatLinkRuleId", source = "bindingRule.id")
    @Mapping(target = "params", source = "bindingRule.params")
    @Mapping(target = "processType", ignore = true)
    TelegramChatLinkRuleOutbox toOutbox(
            TelegramChatLinkRule bindingRule,
            Long linkId,
            Long ruleId,
            OutboxEventType eventType
    );

    @Mapping(target = "id", source = "telegramChatLinkRuleId")
    LinkRuleOutboxDto toOutboxDto(TelegramChatLinkRuleOutbox outbox);
}
