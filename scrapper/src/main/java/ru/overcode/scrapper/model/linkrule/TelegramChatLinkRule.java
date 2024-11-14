package ru.overcode.scrapper.model.linkrule;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.overcode.scrapper.config.converter.RuleParamsConverter;

import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "telegram_chat_link_rule")
public class TelegramChatLinkRule {

    @Id
    private Long id;

    private Long linkId;

    private Long ruleId;

    @Convert(converter = RuleParamsConverter.class)
    private Map<String, String> params;
}
