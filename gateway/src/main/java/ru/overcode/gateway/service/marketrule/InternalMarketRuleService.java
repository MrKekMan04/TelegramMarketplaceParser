package ru.overcode.gateway.service.marketrule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.dto.market.BindRuleRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.mapper.marketrule.MarketRuleMapper;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.gateway.service.market.MarketService;
import ru.overcode.gateway.service.rule.RuleService;

@Service
@RequiredArgsConstructor
public class InternalMarketRuleService {

    private final RuleService ruleService;
    private final MarketService marketDbService;
    private final MarketRuleDbService marketRuleDbService;
    private final MarketRuleMapper marketRuleMapper;

    @Transactional
    public void bindRule(Long marketId, BindRuleRequest request) {
        Market market = marketDbService.findByIdOrElseThrow(marketId);
        Rule rule = ruleService.findByIdOrElseThrow(request.id());

        marketRuleDbService.findByMarketIdAndRuleId(market.getId(), rule.getId())
                .ifPresent(binding -> {
                    throw new UnprocessableEntityException(
                            GatewayExceptionMessage.MARKET_RULE_ALREADY_BOUND
                                    .withParam("marketId", binding.getMarketId().toString())
                                    .withParam("ruleId", binding.getRuleId().toString())
                    );
                });

        marketRuleDbService.save(marketRuleMapper.toEntity(market.getId(), rule.getId()));
    }
}
