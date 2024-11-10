package ru.overcode.scrapper.model.linkrule;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long linkId;

    private Long ruleId;

    @Convert(converter = RuleParamsConverter.class)
    private Map<String, String> params;
}
