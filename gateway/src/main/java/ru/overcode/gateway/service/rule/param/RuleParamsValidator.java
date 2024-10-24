package ru.overcode.gateway.service.rule.param;

import java.util.Map;
import java.util.Set;

public interface RuleParamsValidator {

    Long getRuleId();

    boolean validate(Map<String, String> ruleParams);

    Set<String> getParamNames();

    String getRuleName(String rawName, Map<String, String> ruleParams);
}
