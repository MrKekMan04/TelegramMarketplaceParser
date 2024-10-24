package ru.overcode.gateway.service.marketrule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.marketrule.MarketRule;
import ru.overcode.gateway.repository.marketrule.MarketRuleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketRuleDbService {

    private final MarketRuleRepository marketRuleRepository;

    @Transactional(readOnly = true)
    public Optional<MarketRule> findByMarketIdAndRuleId(Long marketId, Long ruleId) {
        return marketRuleRepository.findByMarketIdAndRuleId(marketId, ruleId);
    }
}
