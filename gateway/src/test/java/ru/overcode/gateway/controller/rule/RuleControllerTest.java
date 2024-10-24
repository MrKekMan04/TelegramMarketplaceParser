package ru.overcode.gateway.controller.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.overcode.gateway.dto.rule.AddRuleRequest;
import ru.overcode.gateway.dto.rule.GetRulesResponse;
import ru.overcode.gateway.dto.rule.RemoveRuleRequest;
import ru.overcode.gateway.dto.rule.RuleDto;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.exception.handler.RestExceptionHandler;
import ru.overcode.gateway.mapper.rest.ResponseMapper;
import ru.overcode.gateway.service.rule.RuleService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.overcode.gateway.util.TestUtils.getErrorPath;


@WebMvcTest({RuleController.class, RestExceptionHandler.class, ResponseMapper.class})
@AutoConfigureMockMvc
public class RuleControllerTest {

    private static final String RULE_URL = "/api/v1/links/{linkId}/rules";
    private static final String REMOVE_RULE_URL = RULE_URL + "/{ruleId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RuleService ruleService;

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при валидных данных")
    public void getRules_shouldReturnOk_whenAllDataIsValid() throws Exception {
        final Long linkId = 1L;

        doReturn(List.of(
                new GetRulesResponse(1L, "desc", Set.of("amount"))
        )).when(ruleService).getRules(any());

        mockMvc.perform(get(RULE_URL, linkId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("GET " + RULE_URL + " - проверка контракта при отрицательном linkId")
    public void getRules_shouldReturnBadRequest_whenLinkIdIsNegative() throws Exception {
        final Long linkId = -1L;

        mockMvc.perform(get(RULE_URL, linkId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`linkId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при валидных данных")
    public void addRule_shouldReturnOk_whenAllDataIsValid() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        doReturn(new RuleDto(1L, "desc"))
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом chatId")
    public void addRule_shouldReturnBadRequest_whenChatIdIsNull() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(null, 1L, Map.of());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при отрицательном chatId")
    public void addRule_shouldReturnBadRequest_whenChatIdIsNegative() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(-1L, 1L, Map.of());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при отрицательном linkId")
    public void addRule_shouldReturnBadRequest_whenLinkIdIsNegative() throws Exception {
        final Long linkId = -1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`linkId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом ruleId")
    public void addRule_shouldReturnBadRequest_whenRuleIdIsNull() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, null, Map.of());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`ruleId` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при отрицательном ruleId")
    public void addRule_shouldReturnBadRequest_whenRuleIdIsNegative() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, -1L, Map.of());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`ruleId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при пустом ruleParams")
    public void addRule_shouldReturnBadRequest_whenRuleParamsIsNull() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, null);

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`ruleParams` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при ruleParams.size > 50")
    public void addRule_shouldReturnBadRequest_whenRuleParamsSizeIsMoreThenFifty() throws Exception {
        final Long linkId = 1L;
        final HashMap<String, String> ruleParams = new HashMap<>();
        IntStream.range(0, 51)
                .boxed()
                .forEach(i -> ruleParams.put(i.toString(), i.toString()));
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, ruleParams);

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("Размер `ruleParams` - максимум 50")).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при ненайденном чате")
    public void addRule_shouldReturnUnprocessableEntity_whenChatNotFound() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.CHAT_NOT_FOUND
                .withParam("chatId", request.chatId().toString())))
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.CHAT_NOT_FOUND.getMessage()
                        .replace("{chatId}", request.chatId().toString()))).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при несуществующей ссылке")
    public void addRule_shouldReturnUnprocessableEntity_whenLinkNotFound() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_FOUND
                .withParam("linkId", linkId.toString())))
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.LINK_NOT_FOUND.getMessage()
                        .replace("{linkId}", linkId.toString()))).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при неотслеживаемой ссылке")
    public void addRule_shouldReturnUnprocessableEntity_whenLinkNotAdded() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_ADDED
                .withParam("linkId", linkId.toString())))
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.LINK_NOT_ADDED.getMessage()
                        .replace("{linkId}", linkId.toString()))).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при несуществующем правиле")
    public void addRule_shouldReturnUnprocessableEntity_whenRuleNotFound() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.RULE_NOT_FOUND
                .withParam("ruleId", request.ruleId().toString())))
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.RULE_NOT_FOUND.getMessage()
                        .replace("{ruleId}", request.ruleId().toString()))).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при уже отслеживаемом правиле")
    public void addRule_shouldReturnUnprocessableEntity_whenRuleAlreadyAdded() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.RULE_ALREADY_ADDED
                .withParam("ruleId", request.ruleId().toString())))
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.RULE_ALREADY_ADDED.getMessage()
                        .replace("{ruleId}", request.ruleId().toString()))).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при некорректных параметрах правила")
    public void addRule_shouldReturnUnprocessableEntity_whenRuleParamsIsIncorrect() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());
        final Set<String> expectedParams = Set.of("param");

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.RULE_BAD_PARAMS
                .withParam("ruleId", request.ruleId().toString())
                .withParam("expectedParams", request.ruleParams().values().toString())
                .withParam("actualParams", expectedParams.toString())))
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.RULE_BAD_PARAMS.getMessage()
                        .replace("{ruleId}", request.ruleId().toString())
                        .replace("{expectedParams}", request.ruleParams().values().toString())
                        .replace("{actualParams}", expectedParams.toString()))).exists());
    }

    @Test
    @DisplayName("POST " + RULE_URL + " - проверка контракта при непредвиденной ошибке")
    public void addRule_shouldReturnInternalError_whenServerError() throws Exception {
        final Long linkId = 1L;
        final AddRuleRequest request = new AddRuleRequest(1L, 1L, Map.of());

        doThrow(new RuntimeException())
                .when(ruleService).addRule(any(), any(), any(), any());

        mockMvc.perform(post(RULE_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.INTERNAL_SERVER.getMessage())).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при валидных данных")
    public void removeRule_shouldReturnOk_whenAllDataIsValid() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при отрицательном linkId")
    public void removeRule_shouldReturnBadRequest_whenLinkIdIsNegative() throws Exception {
        final Long linkId = -1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`linkId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при отрицательном ruleId")
    public void removeRule_shouldReturnBadRequest_whenRuleIdIsNegative() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = -1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`ruleId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при пустом chatId")
    public void removeRule_shouldReturnBadRequest_whenChatIdIsNull() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(null);

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при отрицательном chatId")
    public void removeRule_shouldReturnBadRequest_whenChatIdIsNegative() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(-1L);

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при ненайденном чате")
    public void removeRule_shouldReturnUnprocessableEntity_whenChatNotFound() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.CHAT_NOT_FOUND
                .withParam("chatId", request.chatId().toString())))
                .when(ruleService).removeRule(any(), any(), any());

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.CHAT_NOT_FOUND.getMessage())
                        .replace("{chatId}", request.chatId().toString())).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при ненайденной ссылке")
    public void removeRule_shouldReturnUnprocessableEntity_whenLinkNotFound() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_FOUND
                .withParam("linkId", linkId.toString())))
                .when(ruleService).removeRule(any(), any(), any());

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.LINK_NOT_FOUND.getMessage())
                        .replace("{linkId}", linkId.toString())).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при неотслеживаемой ссылке")
    public void removeRule_shouldReturnUnprocessableEntity_whenLinkNotAdded() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_ADDED
                .withParam("linkId", linkId.toString())))
                .when(ruleService).removeRule(any(), any(), any());

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.LINK_NOT_ADDED.getMessage())
                        .replace("{linkId}", linkId.toString())).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при ненайденном правиле")
    public void removeRule_shouldReturnUnprocessableEntity_whenRuleNotFound() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.RULE_NOT_FOUND
                .withParam("ruleId", ruleId.toString())))
                .when(ruleService).removeRule(any(), any(), any());

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.RULE_NOT_FOUND.getMessage())
                        .replace("{ruleId}", ruleId.toString())).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при неотслеживаемом правиле")
    public void removeRule_shouldReturnUnprocessableEntity_whenRuleNotAdded() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.RULE_NOT_ADDED
                .withParam("ruleId", ruleId.toString())))
                .when(ruleService).removeRule(any(), any(), any());

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.RULE_NOT_ADDED.getMessage())
                        .replace("{ruleId}", ruleId.toString())).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_RULE_URL + " - проверка контракта при непредвиденной ошибке")
    public void removeRule_shouldReturnInternalError_whenServerError() throws Exception {
        final Long linkId = 1L;
        final Long ruleId = 1L;
        final RemoveRuleRequest request = new RemoveRuleRequest(1L);

        doThrow(new RuntimeException())
                .when(ruleService).removeRule(any(), any(), any());

        mockMvc.perform(delete(REMOVE_RULE_URL, linkId, ruleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.INTERNAL_SERVER.getMessage())).exists());
    }
}
