package ru.overcode.gateway.service.rule;

import org.springframework.stereotype.Service;
import ru.overcode.gateway.dto.rule.RuleDto;

import java.util.Map;

@Service
public class RuleService {

    public RuleDto addRule(Long chatId, Long linkId, Long ruleId, Map<String, String> ruleParams) {
        return new RuleDto(2L, "Цена ниже 500");
    }

    public void removeRule(Long chatId, Long linkId, Long ruleId) {

    }
}
