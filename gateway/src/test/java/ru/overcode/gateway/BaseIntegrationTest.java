package ru.overcode.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.dto.market.MarketName;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.gateway.model.telegramchat.TelegramChat;
import ru.overcode.gateway.repository.chatlink.TelegramChatLinkRepository;
import ru.overcode.gateway.repository.chatlink.rule.TelegramChatLinkRuleRepository;
import ru.overcode.gateway.repository.link.LinkRepository;
import ru.overcode.gateway.repository.market.MarketRepository;
import ru.overcode.gateway.repository.rule.RuleRepository;
import ru.overcode.gateway.repository.telegramchat.TelegramChatRepository;

import java.net.URI;
import java.util.Map;

@SpringBootTest
@Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseIntegrationTest {

    @Autowired
    protected TelegramChatRepository telegramChatRepository;
    @Autowired
    protected LinkRepository linkRepository;
    @Autowired
    protected TelegramChatLinkRepository bindingRepository;
    @Autowired
    protected MarketRepository marketRepository;
    @Autowired
    protected RuleRepository ruleRepository;
    @Autowired
    protected TelegramChatLinkRuleRepository bindingRuleRepository;

    protected void createTelegramChat(Long chatId) {
        telegramChatRepository.save(new TelegramChat()
                .setId(chatId));
    }

    protected Link createLink(URI url) {
        return linkRepository.save(new Link()
                .setUrl(url)
                .setMarketId(RandomUtils.nextLong()));
    }

    protected TelegramChatLink createBinding(Long chatId, Long linkId) {
        return bindingRepository.save(new TelegramChatLink()
                .setChatId(chatId)
                .setLinkId(linkId));
    }

    protected Market createMarket(String host) {
        return marketRepository.save(new Market()
                .setName(MarketName.WILDBERRIES)
                .setUrlDomain(host));
    }

    protected Rule createRule() {
        return ruleRepository.save(new Rule()
                .setName(RandomStringUtils.randomAlphabetic(5))
                .setDescription(RandomStringUtils.randomAlphabetic(5)));
    }

    protected TelegramChatLinkRule createBindingRule(Long bindingId, Long ruleId, Map<String, String> params) {
        return bindingRuleRepository.save(new TelegramChatLinkRule()
                .setChatLinkId(bindingId)
                .setRuleId(ruleId)
                .setParams(params));
    }
}
