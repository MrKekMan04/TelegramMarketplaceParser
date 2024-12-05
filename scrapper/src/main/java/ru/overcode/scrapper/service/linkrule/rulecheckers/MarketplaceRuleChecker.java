package ru.overcode.scrapper.service.linkrule.rulecheckers;

import ru.overcode.scrapper.dto.ProductDto;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;

public interface MarketplaceRuleChecker {

    Long getRuleId();

    boolean validateRuleParams(TelegramChatLinkRule telegramChatLinkRule, ProductDto productDto);
}
