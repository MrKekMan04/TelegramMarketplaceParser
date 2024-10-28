package ru.overcode.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.testcontainers.utility.DockerImageName;
import ru.overcode.gateway.model.chatlink.TelegramChatLink;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRule;
import ru.overcode.gateway.model.link.Link;
import ru.overcode.gateway.model.market.Market;
import ru.overcode.gateway.model.marketrule.MarketRule;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.gateway.model.telegramchat.TelegramChat;
import ru.overcode.gateway.repository.chatlink.TelegramChatLinkRepository;
import ru.overcode.gateway.repository.chatlink.rule.TelegramChatLinkRuleRepository;
import ru.overcode.gateway.repository.link.LinkRepository;
import ru.overcode.gateway.repository.market.MarketRepository;
import ru.overcode.gateway.repository.marketrule.MarketRuleRepository;
import ru.overcode.gateway.repository.rule.RuleRepository;
import ru.overcode.gateway.repository.telegramchat.TelegramChatRepository;
import ru.overcode.gateway.service.rule.RuleDbService;
import ru.overcode.shared.dto.market.MarketName;

import java.net.URI;
import java.util.Map;

@Testcontainers
@SpringBootTest
@Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseIntegrationTest {

    @Container
    public static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"))
                    .withReuse(true);

    @DynamicPropertySource
    public static void configureKafka(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

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
    @Autowired
    protected MarketRuleRepository marketRuleRepository;
    @Autowired
    private RuleDbService ruleDbService;

    protected void createTelegramChat(Long chatId) {
        telegramChatRepository.save(new TelegramChat()
                .setId(chatId));
    }

    protected Link createLink(URI url) {
        return createLink(url, RandomUtils.nextLong());
    }

    protected Link createLink(URI url, Long marketId) {
        return linkRepository.save(new Link()
                .setUrl(url)
                .setMarketId(marketId));
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

    protected void createRule(Long ruleId) {
        ruleDbService.saveWithId(
                ruleId,
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );
    }

    protected TelegramChatLinkRule createBindingRule(Long bindingId, Long ruleId, Map<String, String> params) {
        return bindingRuleRepository.save(new TelegramChatLinkRule()
                .setChatLinkId(bindingId)
                .setRuleId(ruleId)
                .setParams(params));
    }

    protected void createMarketRule(Long marketId, Long ruleId) {
        marketRuleRepository.save(new MarketRule()
                .setMarketId(marketId)
                .setRuleId(ruleId));
    }
}
