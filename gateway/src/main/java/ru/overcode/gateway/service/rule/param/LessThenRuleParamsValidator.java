package ru.overcode.gateway.service.rule.param;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class LessThenRuleParamsValidator implements RuleParamsValidator {

    private static final Long RULE_ID = 1L;
    private static final String AMOUNT_KEY = "amount";
    private static final Set<String> PARAM_NAMES = Set.of(AMOUNT_KEY);

    @Override
    public Long getRuleId() {
        return RULE_ID;
    }

    @Override
    public boolean validate(Map<String, String> ruleParams) {
        return PARAM_NAMES.stream().allMatch(ruleParams::containsKey)
                && isValidNumber(ruleParams.get(AMOUNT_KEY));
    }

    @Override
    public Set<String> getParamNames() {
        return PARAM_NAMES;
    }

    @Override
    public String getRuleName(String rawName, Map<String, String> ruleParams) {
        return rawName.formatted(ruleParams.get(AMOUNT_KEY));
    }

    private boolean isValidNumber(String number) {
        try {
            return Long.parseLong(number) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
