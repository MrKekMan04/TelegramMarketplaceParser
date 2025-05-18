package ru.overcode.gateway.service.marketrule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.dto.market.BindRuleRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.model.marketrule.MarketRule;
import ru.overcode.gateway.model.rule.Rule;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InternalMarketRuleServiceTest extends BaseIntegrationTest {

    @Autowired
    private InternalMarketRuleService internalMarketRuleService;

    @Test
    @DisplayName("Маркетплейс успешно привязывается к правилу при валидных данных")
    public void bindRule_shouldBindRuleToMarket_whenAllDataIsValid() {
        Market market = createMarket("www.wildberries.ru");
        Rule rule = createRule();

        BindRuleRequest request = new BindRuleRequest(rule.getId());

        internalMarketRuleService.bindRule(market.getId(), request);

        Optional<MarketRule> optionalBinding =
                marketRuleRepository.findByMarketIdAndRuleId(market.getId(), rule.getId());
        assertTrue(optionalBinding.isPresent());
    }

    @Test
    @DisplayName("Маркетплейс не найден при привязке")
    public void bindRule_shouldThrow_whenmarketNotFound() {
        Long marketId = RandomUtils.nextLong();
        Rule rule = createRule();

        BindRuleRequest request = new BindRuleRequest(rule.getId());

        assertThrows(
                UnprocessableEntityException.class,
                () -> internalMarketRuleService.bindRule(marketId, request),
                GatewayExceptionMessage.MARKET_RULE_ALREADY_BOUND.getMessage()
                        .replace("{marketId}", marketId.toString())
                        .replace("{ruleId}", rule.getId().toString())
        );

        Optional<MarketRule> optionalBinding =
                marketRuleRepository.findByMarketIdAndRuleId(marketId, rule.getId());
        assertFalse(optionalBinding.isPresent());
    }

    @Test
    @DisplayName("Правило не найдено при привязке")
    public void bindRule_shouldThrow_whenRuleNotFound() {
        Long ruleId = RandomUtils.nextLong();
        Market market = createMarket("www.wildberries.ru");

        BindRuleRequest request = new BindRuleRequest(ruleId);

        assertThrows(
                UnprocessableEntityException.class,
                () -> internalMarketRuleService.bindRule(market.getId(), request),
                GatewayExceptionMessage.MARKET_RULE_ALREADY_BOUND.getMessage()
                        .replace("{marketId}", market.getId().toString())
                        .replace("{ruleId}", ruleId.toString())
        );

        Optional<MarketRule> optionalBinding =
                marketRuleRepository.findByMarketIdAndRuleId(market.getId(), ruleId);
        assertFalse(optionalBinding.isPresent());
    }

    @Test
    @DisplayName("Маркетплейс уже привязан к правилу")
    public void bindRule_shouldThrow_whenMarketAlreadyBound() {
        Market market = createMarket("www.wildberries.ru");
        Rule rule = createRule();
        createMarketRule(market.getId(), rule.getId());

        BindRuleRequest request = new BindRuleRequest(rule.getId());

        assertThrows(
                UnprocessableEntityException.class,
                () -> internalMarketRuleService.bindRule(market.getId(), request),
                GatewayExceptionMessage.MARKET_RULE_ALREADY_BOUND.getMessage()
                        .replace("{marketId}", market.getId().toString())
                        .replace("{ruleId}", rule.getId().toString())
        );
    }
}
