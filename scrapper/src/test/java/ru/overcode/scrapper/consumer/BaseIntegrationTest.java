package ru.overcode.scrapper.consumer;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.overcode.scrapper.consumer.config.KafkaConfig;
import ru.overcode.scrapper.model.link.Link;
import ru.overcode.scrapper.model.linkrule.TelegramChatLinkRule;
import ru.overcode.scrapper.repository.link.LinkRepository;
import ru.overcode.scrapper.repository.linkrule.TelegramChatLinkRuleRepository;
import ru.overcode.shared.dto.market.MarketName;

import java.net.URI;
import java.util.Map;

@SpringBootTest
@Import(KafkaConfig.class)
@Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BaseIntegrationTest {
    public static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"))
                    .withReuse(true);

    @BeforeAll
    public static void turnOnContainers() {
        KAFKA.start();
    }

    @DynamicPropertySource
    public static void configureKafka(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    protected LinkRepository linkRepository;

    @Autowired
    protected TelegramChatLinkRuleRepository telegramChatLinkRuleRepository;

    protected void createLink(Long id, URI url) {
        linkRepository.save(new Link()
                .setId(id)
                .setUrl(url)
                .setMarketName(MarketName.WILDBERRIES));
    }

    protected void createLinkRule(Long id, Long linkId, Long ruleId, Map<String, String> params) {
        telegramChatLinkRuleRepository.save(new TelegramChatLinkRule()
                .setId(id)
                .setLinkId(linkId)
                .setRuleId(ruleId)
                .setParams(params));
    }
}
