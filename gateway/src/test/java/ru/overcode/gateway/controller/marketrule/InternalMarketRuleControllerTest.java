package ru.overcode.gateway.controller.marketrule;

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
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import ru.overcode.gateway.dto.market.BindRuleRequest;
import ru.overcode.gateway.exception.handler.RestExceptionHandler;
import ru.overcode.gateway.mapper.rest.ResponseMapper;
import ru.overcode.gateway.service.marketrule.InternalMarketRuleService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.overcode.gateway.util.TestUtils.getErrorPath;

@WebMvcTest({InternalMarketRuleController.class, RestExceptionHandler.class, ResponseMapper.class})
@AutoConfigureMockMvc(addFilters = false)
public class InternalMarketRuleControllerTest {

    private static final String MARKET_RULE_URL = "/internal/api/v1/markets/{marketId}/rules";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InternalMarketRuleService internalMarketRuleService;

    @AfterEach
    public void resetMocks() {
        Mockito.reset(internalMarketRuleService);
    }

    @Test
    @DisplayName("POST " + MARKET_RULE_URL + " - проверка контракта при валидных данных")
    public void bindRule_shouldReturnOk_whenAllDataIsValid() throws Exception {
        Long marketId = RandomUtils.nextLong();
        BindRuleRequest request = new BindRuleRequest(RandomUtils.nextLong());

        mockMvc.perform(post(MARKET_RULE_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("POST " + MARKET_RULE_URL + " - проверка контракта при отрицательном marketId")
    public void bindRule_shouldReturnBadRequest_whenMarketIdIsNegative() throws Exception {
        Long marketId = -RandomUtils.nextLong();
        BindRuleRequest request = new BindRuleRequest(RandomUtils.nextLong());

        mockMvc.perform(post(MARKET_RULE_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`marketId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + MARKET_RULE_URL + " - проверка контракта при пустом ruleId")
    public void bindRule_shouldReturnBadRequest_whenRuleIdIsNull() throws Exception {
        Long marketId = RandomUtils.nextLong();
        BindRuleRequest request = new BindRuleRequest(null);

        mockMvc.perform(post(MARKET_RULE_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`id` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + MARKET_RULE_URL + " - проверка контракта при отрицательном ruleId")
    public void bindRule_shouldReturnBadRequest_whenRuleIdIsNegative() throws Exception {
        Long marketId = RandomUtils.nextLong();
        BindRuleRequest request = new BindRuleRequest(-RandomUtils.nextLong());

        mockMvc.perform(post(MARKET_RULE_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`id` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + MARKET_RULE_URL + " - проверка контракта при непредвиденной ошибке")
    public void createMarket_shouldReturnInternalError_whenServerError() throws Exception {
        doThrow(new RuntimeException()).when(internalMarketRuleService).bindRule(any(), any());

        BindRuleRequest request = new BindRuleRequest(RandomUtils.nextLong());

        mockMvc.perform(post(MARKET_RULE_URL, RandomUtils.nextLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
