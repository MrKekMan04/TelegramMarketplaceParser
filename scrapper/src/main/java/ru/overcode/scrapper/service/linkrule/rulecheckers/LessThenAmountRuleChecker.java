package ru.overcode.scrapper.service.linkrule.rulecheckers;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.overcode.scrapper.dto.ProductDto;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class LessThenAmountRuleChecker implements MarketplaceRuleChecker {

    private static final String AMOUNT_KEY = "amount";
    private static final Long RULE_ID = 1L;

    @Override
    public Long getRuleId() {
        return RULE_ID;
    }

    @Override
    public boolean validateRuleParams(TelegramChatLinkRule telegramChatLinkRule, ProductDto productDto) {
        if (productDto == null || productDto.getPrice() == null
                || CollectionUtils.isEmpty(telegramChatLinkRule.getParams())) {
            return false;
        }

        Map<String, String> params = telegramChatLinkRule.getParams();

        try {
            return new BigDecimal(params.get(AMOUNT_KEY)).compareTo(productDto.getPrice()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
