package ru.overcode.gateway.repository.marketrule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.marketrule.MarketRule;

import java.util.Optional;

@Repository
public interface MarketRuleRepository extends JpaRepository<MarketRule, Long> {

    Optional<MarketRule> findByMarketIdAndRuleId(Long marketId, Long ruleId);
}
