package ru.overcode.scrapper.service.schedule.link.marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import ru.overcode.scrapper.dto.ProductDto;
import ru.overcode.scrapper.model.link.Link;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;
import ru.overcode.scrapper.producer.LinkUpdateProducer;
import ru.overcode.scrapper.service.link.LinkService;
import ru.overcode.scrapper.service.linkrule.TelegramChatLinkRuleService;
import ru.overcode.scrapper.service.linkrule.rulecheckers.MarketplaceRuleChecker;
import ru.overcode.scrapper.service.marketplace.WildberriesService;
import ru.overcode.scrapper.service.schedule.link.MarketplaceProcessor;
import ru.overcode.shared.dto.market.MarketName;
import ru.overcode.shared.stream.update.ScrapperLinkUpdateDto;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WildberriesProcessor implements MarketplaceProcessor {

    private final LinkService linkService;
    private final TelegramChatLinkRuleService telegramChatLinkRuleService;
    private final WildberriesService wildberriesService;
    private final LinkUpdateProducer linkUpdateProducer;
    @Qualifier("marketplaceRuleCheckersById")
    private final Map<Long, MarketplaceRuleChecker> ruleCheckers;

    @Value("${feign.wildberries.link.batch-size}")
    private final int linksBatchSize;
    @Value("${feign.wildberries.rule.batch-size}")
    private final int rulesBatchSize;

    @Override
    public MarketName getMarketName() {
        return MarketName.WILDBERRIES;
    }

    @Override
    public void process() {
        Slice<Link> linksSlice;
        Pageable pageRequest = PageRequest.of(0, linksBatchSize);
        do {
            linksSlice = linkService.findLinksByMarketName(MarketName.WILDBERRIES, pageRequest);

            List<Link> links = linksSlice.getContent();
            Map<Long, ProductDto> productDtoByLinkId = wildberriesService.fetchProducts(links);
            if (!productDtoByLinkId.isEmpty()) {
                links.forEach(link -> processRulesInBatches(link, productDtoByLinkId.get(link.getId())));
            }

            pageRequest = pageRequest.next();
        } while (linksSlice.hasNext());
    }


    private void processRulesInBatches(Link link, ProductDto productDto) {
        Slice<TelegramChatLinkRule> rulesSlice;
        Pageable rulePageRequest = PageRequest.of(0, rulesBatchSize);
        do {
            rulesSlice = telegramChatLinkRuleService.findTelegramChatLinkRulesByLinkId(link.getId(), rulePageRequest);

            rulesSlice.getContent().stream()
                    .filter(rule -> ruleCheckers.get(rule.getRuleId()).validateRuleParams(rule, productDto))
                    .map(TelegramChatLinkRule::getId)
                    .map(ScrapperLinkUpdateDto::new)
                    .forEach(linkUpdateProducer::send);

            rulePageRequest = rulePageRequest.next();
        } while (rulesSlice.hasNext());
    }
}
