package ru.overcode.gateway.mapper.rule;

import org.mapstruct.Mapper;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.dto.chatlink.rule.LinkRuleDto;
import ru.overcode.gateway.dto.rule.GetRulesResponse;
import ru.overcode.gateway.dto.rule.RuleDto;
import ru.overcode.gateway.model.rule.Rule;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = MappersConfig.class)
public interface RuleMapper {

    RuleDto toRuleDto(LinkRuleDto dto);

    default List<GetRulesResponse> toGetRulesResponse(List<Rule> rules, Map<Long, Set<String>> params) {
        return rules.stream()
                .map(rule -> new GetRulesResponse(rule.getId(), rule.getDescription(), params.get(rule.getId())))
                .toList();
    }

    default Map<String, String> fetchValidParams(Set<String> validParamNames, Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> validParamNames.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
