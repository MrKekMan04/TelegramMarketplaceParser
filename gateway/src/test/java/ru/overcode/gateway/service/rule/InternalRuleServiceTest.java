package ru.overcode.gateway.service.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.BaseIntegrationTest;
import ru.overcode.gateway.dto.page.PagedRequest;
import ru.overcode.gateway.dto.page.SortRequest;
import ru.overcode.gateway.dto.rule.internal.CreateRuleRequest;
import ru.overcode.gateway.dto.rule.internal.InternalRuleDto;
import ru.overcode.gateway.dto.rule.internal.UpdateRuleRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.model.rule.Rule;
import ru.overcode.shared.api.PageContent;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class InternalRuleServiceTest extends BaseIntegrationTest {

    @Autowired
    private InternalRuleService internalRuleService;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    @DisplayName("Правила возвращаются в несортированном виде страницей, если сортировка не задана")
    public void getRules_shouldReturnUnsortedPage_whenSortIsNotConfigured(int page) {
        PagedRequest pageable = new PagedRequest(page, 2, null);

        List<Rule> rules = IntStream.range(0, 5)
                .mapToObj(i -> createRule((long) i, String.valueOf(i)))
                .toList();
        List<Rule> expectedRules = rules.stream()
                .skip((long) pageable.items() * pageable.page())
                .limit(pageable.items())
                .toList();

        PageContent<InternalRuleDto> pageContent = internalRuleService.getRules(pageable);

        assertEquals(expectedRules.size(), pageContent.content().size());
        assertEquals(3, pageContent.totalPages());
        IntStream.range(0, expectedRules.size())
                .forEach(i -> {
                    assertEquals(expectedRules.get(i).getId(), pageContent.content().get(i).id());
                    assertEquals(expectedRules.get(i).getName(), pageContent.content().get(i).name());
                    assertEquals(expectedRules.get(i).getDescription(), pageContent.content().get(i).description());
                });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    @DisplayName("Правила возвращаются в отсортированном по колонке виде страницей, если сортировка задана")
    public void getRules_shouldReturnSortedPage_whenSortIsConfigured(int page) {
        PagedRequest pageable = new PagedRequest(page, 2, new SortRequest("id", Sort.Direction.DESC));

        List<Rule> rules = IntStream.range(0, 5)
                .mapToObj(i -> createRule((long) i, String.valueOf(i)))
                .toList();
        List<Rule> expectedRules = rules.reversed().stream()
                .skip((long) pageable.items() * pageable.page())
                .limit(pageable.items())
                .toList();

        PageContent<InternalRuleDto> pageContent = internalRuleService.getRules(pageable);

        assertEquals(expectedRules.size(), pageContent.content().size());
        assertEquals(3, pageContent.totalPages());
        IntStream.range(0, expectedRules.size())
                .forEach(i -> {
                    assertEquals(expectedRules.get(i).getId(), pageContent.content().get(i).id());
                    assertEquals(expectedRules.get(i).getName(), pageContent.content().get(i).name());
                    assertEquals(expectedRules.get(i).getDescription(), pageContent.content().get(i).description());
                });
    }

    @Test
    @DisplayName("Правило создается")
    public void createRule_shouldCreateRule_whenAllDataIsValid() {
        CreateRuleRequest request = new CreateRuleRequest(
                RandomUtils.nextLong(),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        internalRuleService.createRule(request);

        Optional<Rule> rule = ruleRepository.findById(request.id());

        assertTrue(rule.isPresent());
        assertEquals(request.name(), rule.get().getName());
        assertEquals(request.description(), rule.get().getDescription());
    }

    @Test
    @DisplayName("Выбрасывается исключение, если правило уже создано")
    public void createRule_shouldThrows_whenRuleExists() {
        Rule rule = createRule();
        CreateRuleRequest request = new CreateRuleRequest(
                rule.getId(),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        assertThrows(
                UnprocessableEntityException.class,
                () -> internalRuleService.createRule(request),
                GatewayExceptionMessage.RULE_EXISTS.getMessage()
                        .replace("{ruleId}", rule.getId().toString())
        );
    }

    @Test
    @DisplayName("Правило обновляется")
    public void updateRule_shouldUpdateRule_whenAllDataIsValid() {
        Rule rule = createRule();
        UpdateRuleRequest request = new UpdateRuleRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        internalRuleService.updateRule(rule.getId(), request);

        Optional<Rule> updatedRule = ruleRepository.findById(rule.getId());

        assertTrue(updatedRule.isPresent());
        assertEquals(request.name(), updatedRule.get().getName());
        assertEquals(request.description(), updatedRule.get().getDescription());
    }

    @Test
    @DisplayName("Выбрасывается исключение, если правило не найдено")
    public void createRule_shouldThrows_whenRuleNotFound() {
        Long ruleId = RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        assertThrows(
                UnprocessableEntityException.class,
                () -> internalRuleService.updateRule(ruleId, request),
                GatewayExceptionMessage.RULE_NOT_FOUND.getMessage()
                        .replace("{ruleId}", ruleId.toString())
        );
    }
}
