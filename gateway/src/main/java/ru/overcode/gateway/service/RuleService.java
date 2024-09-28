package ru.overcode.gateway.service;

import org.springframework.stereotype.Service;
import ru.overcode.gateway.dto.link.RuleDto;

import java.util.List;
import java.util.Map;

@Service
public class RuleService {

    public List<RuleDto> getRules(Long linkId) {
        return List.of(
                new RuleDto(1L, "Цена ниже 500")
        );
    }

    public RuleDto addRule(Long chatId, Long linkId, Long ruleId, Map<String, String> ruleParams) {
        return new RuleDto(2L, "Цена ниже 500");
    }

    public void removeRule(Long chatId, Long linkId, Long ruleId) {

    }
}
