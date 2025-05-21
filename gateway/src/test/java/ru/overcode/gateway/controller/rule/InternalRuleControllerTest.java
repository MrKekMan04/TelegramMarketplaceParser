package ru.overcode.gateway.controller.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.dto.rule.internal.CreateRuleRequest;
import ru.overcode.gateway.dto.rule.internal.InternalRuleDto;
import ru.overcode.gateway.dto.rule.internal.UpdateRuleRequest;
import ru.overcode.gateway.exception.handler.RestExceptionHandler;
import ru.overcode.gateway.mapper.rest.ResponseMapper;
import ru.overcode.gateway.service.rule.InternalRuleService;
import ru.overcode.shared.api.PageContent;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.overcode.gateway.util.TestUtils.getErrorPath;

@WebMvcTest({InternalRuleController.class, RestExceptionHandler.class, ResponseMapper.class})
@AutoConfigureMockMvc(addFilters = false)
public class InternalRuleControllerTest {

    private static final String RULE_URL = "/internal/api/v1/rules";
    private static final String UPDATE_RULE_URL = RULE_URL + "/{ruleId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InternalRuleService internalRuleService;

    @AfterEach
    public void resetMocks() {
        Mockito.reset(internalRuleService);
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при валидных данных. Сортировка отсутствует")
    public void getRules_shouldReturnOk_whenAllDataValidAndSortNotConfigured() throws Exception {
        doReturn(new PageContent<>(1, List.of(
                new InternalRuleDto(null, null, null)
        ))).when(internalRuleService).getRules(any());

        mockMvc.perform(get(RULE_URL)
                        .param("page", "0")
                        .param("items", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при валидных данных. Сортировка указана")
    public void getRules_shouldReturnOk_whenAllDataValidAndSortConfigured() throws Exception {
        doReturn(new PageContent<>(1, List.of(
                new InternalRuleDto(null, null, null)
        ))).when(internalRuleService).getRules(any());

        mockMvc.perform(get(RULE_URL)
                        .param("page", "0")
                        .param("items", "1")
                        .param("sort.order", "ASC")
                        .param("sort.column", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при отрицательном page")
    public void getRules_shouldReturnBadRequest_whenPageIsNegative() throws Exception {
        mockMvc.perform(get(RULE_URL)
                        .param("page", "-1")
                        .param("items", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`page` должен быть больше или равен 0")).exists());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при items < 1")
    public void getRules_shouldReturnBadRequest_whenItemsIsLessThenOne() throws Exception {
        mockMvc.perform(get(RULE_URL)
                        .param("page", "0")
                        .param("items", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`items` должен быть больше или равен 1")).exists());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при items > 500")
    public void getRules_shouldReturnBadRequest_whenItemsIsMoreThenFiveHundred() throws Exception {
        mockMvc.perform(get(RULE_URL)
                        .param("page", "0")
                        .param("items", "501"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`items` должен быть меньше лии равен 500")).exists());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при неуказанном порядке сортировке")
    public void getRules_shouldReturnBadRequest_whenSortOrderIsNull() throws Exception {
        mockMvc.perform(get(RULE_URL)
                        .param("page", "0")
                        .param("items", "1")
                        .param("sort.order", "")
                        .param("sort.column", "id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`order` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при неуказанной колонке сортировки")
    public void getRules_shouldReturnBadRequest_whenSortColumnIsBlank() throws Exception {
        mockMvc.perform(get(RULE_URL)
                        .param("page", "0")
                        .param("items", "1")
                        .param("sort.order", "ASC")
                        .param("sort.column", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`column` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при непредвиденной ошибке")
    public void getRules_shouldReturnInternalError_whenServerError() throws Exception {
        doThrow(new RuntimeException()).when(internalRuleService).getRules(any());

        mockMvc.perform(get(RULE_URL)
                        .param("page", "0")
                        .param("items", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при валидных данных")
    public void createRule_shouldReturnOk_whenAllDataIsValid() throws Exception {
        CreateRuleRequest request = new CreateRuleRequest(
                RandomUtils.nextLong(),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при отрицательном id")
    public void createRule_shouldReturnBadRequest_whenIdIsNegative() throws Exception {
        CreateRuleRequest request = new CreateRuleRequest(
                -RandomUtils.nextLong(),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`id` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом id")
    public void createRule_shouldReturnBadRequest_whenIdIsNull() throws Exception {
        CreateRuleRequest request = new CreateRuleRequest(
                null,
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`id` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом name")
    public void createRule_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        CreateRuleRequest request = new CreateRuleRequest(
                RandomUtils.nextLong(),
                "",
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`name` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом name")
    public void createRule_shouldReturnBadRequest_whenNameIsNull() throws Exception {
        CreateRuleRequest request = new CreateRuleRequest(
                RandomUtils.nextLong(),
                null,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`name` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом description")
    public void createRule_shouldReturnBadRequest_whenDescriptionIsNull() throws Exception {
        CreateRuleRequest request = new CreateRuleRequest(
                RandomUtils.nextLong(),
                RandomStringUtils.randomAlphabetic(5),
                null
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`description` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом name")
    public void createRule_shouldReturnBadRequest_whenDescriptionIsBlank() throws Exception {
        CreateRuleRequest request = new CreateRuleRequest(
                RandomUtils.nextLong(),
                RandomStringUtils.randomAlphabetic(5),
                ""
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`description` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при непредвиденной ошибке")
    public void createRule_shouldReturnInternalError_whenServerError() throws Exception {
        doThrow(new RuntimeException()).when(internalRuleService).createRule(any());

        CreateRuleRequest request = new CreateRuleRequest(
                RandomUtils.nextLong(),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(RULE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("PUT " + UPDATE_RULE_URL + " - проверка контракта при валидных данных")
    public void updateRule_shouldReturnOk_whenAllDataIsValid() throws Exception {
        Long ruleId = RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_RULE_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("PUT " + UPDATE_RULE_URL + " - проверка контракта при отрицательном ruleId")
    public void updateRule_shouldReturnBadRequest_whenRuleIdIsNegative() throws Exception {
        Long ruleId = -RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_RULE_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`ruleId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_RULE_URL + " - проверка контракта при пустом name")
    public void updateRule_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        Long ruleId = RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                "",
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_RULE_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`name` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_RULE_URL + " - проверка контракта при пустом name")
    public void updateRule_shouldReturnBadRequest_whenNameIsNull() throws Exception {
        Long ruleId = RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                null,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_RULE_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`name` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_RULE_URL + " - проверка контракта при пустом description")
    public void updateRule_shouldReturnBadRequest_whenDescriptionIsBlank() throws Exception {
        Long ruleId = RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                RandomStringUtils.randomAlphabetic(5),
                ""
        );

        mockMvc.perform(put(UPDATE_RULE_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`description` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_RULE_URL + " - проверка контракта при пустом description")
    public void updateRule_shouldReturnBadRequest_whenDescriptionIsNull() throws Exception {
        Long ruleId = RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                RandomStringUtils.randomAlphabetic(5),
                null
        );

        mockMvc.perform(put(UPDATE_RULE_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`description` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_RULE_URL + " - проверка контракта при непредвиденной ошибке")
    public void updateRule_shouldReturnInternalError_whenServerError() throws Exception {
        doThrow(new RuntimeException()).when(internalRuleService).updateRule(any(), any());

        Long ruleId = RandomUtils.nextLong();
        UpdateRuleRequest request = new UpdateRuleRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_RULE_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
