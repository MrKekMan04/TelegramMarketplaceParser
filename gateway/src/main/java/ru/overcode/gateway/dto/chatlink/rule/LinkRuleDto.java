package ru.overcode.gateway.dto.chatlink.rule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkRuleDto {

    private Long linkId;
    private Long ruleId;
    private String ruleDescription;
}
