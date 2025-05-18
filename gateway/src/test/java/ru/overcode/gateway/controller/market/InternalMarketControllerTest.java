package ru.overcode.gateway.controller.market;

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
import ru.overcode.gateway.dto.market.CreateMarketRequest;
import ru.overcode.gateway.dto.market.InternalMarketDto;
import ru.overcode.gateway.dto.market.UpdateMarketRequest;
import ru.overcode.gateway.exception.handler.RestExceptionHandler;
import ru.overcode.gateway.mapper.rest.ResponseMapper;
import ru.overcode.gateway.service.market.InternalMarketService;
import ru.overcode.shared.api.PageContent;
import ru.overcode.shared.dto.market.MarketName;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.overcode.gateway.util.TestUtils.getErrorPath;

@WebMvcTest({InternalMarketController.class, RestExceptionHandler.class, ResponseMapper.class})
@AutoConfigureMockMvc(addFilters = false)
public class InternalMarketControllerTest {

    private static final String MARKET_URL = "/internal/api/v1/markets";
    private static final String UPDATE_MARKET_URL = MARKET_URL + "/{marketId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InternalMarketService internalMarketService;

    @AfterEach
    public void resetMocks() {
        Mockito.reset(internalMarketService);
    }

    @Test
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при валидных данных. Сортировка отсутствует")
    public void getMarkets_shouldReturnOk_whenAllDataValidAndSortNotConfigured() throws Exception {
        doReturn(new PageContent<>(1, List.of(
                new InternalMarketDto(null, null, null)
        ))).when(internalMarketService).getMarkets(any());

        mockMvc.perform(get(MARKET_URL)
                        .param("page", "0")
                        .param("items", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при валидных данных. Сортировка указана")
    public void getMarkets_shouldReturnOk_whenAllDataValidAndSortConfigured() throws Exception {
        doReturn(new PageContent<>(1, List.of(
                new InternalMarketDto(null, null, null)
        ))).when(internalMarketService).getMarkets(any());

        mockMvc.perform(get(MARKET_URL)
                        .param("page", "0")
                        .param("items", "1")
                        .param("sort.order", "ASC")
                        .param("sort.column", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при отрицательном page")
    public void getMarkets_shouldReturnBadRequest_whenPageIsNegative() throws Exception {
        mockMvc.perform(get(MARKET_URL)
                        .param("page", "-1")
                        .param("items", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`page` должен быть больше или равен 0")).exists());
    }

    @Test
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при items < 1")
    public void getMarkets_shouldReturnBadRequest_whenItemsIsLessThenOne() throws Exception {
        mockMvc.perform(get(MARKET_URL)
                        .param("page", "0")
                        .param("items", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`items` должен быть больше или равен 1")).exists());
    }

    @Test
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при items > 500")
    public void getMarkets_shouldReturnBadRequest_whenItemsIsMoreThenFiveHundred() throws Exception {
        mockMvc.perform(get(MARKET_URL)
                        .param("page", "0")
                        .param("items", "501"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`items` должен быть меньше лии равен 500")).exists());
    }

    @Test
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при неуказанном порядке сортировке")
    public void getMarkets_shouldReturnBadRequest_whenSortOrderIsNull() throws Exception {
        mockMvc.perform(get(MARKET_URL)
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
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при неуказанной колонке сортировки")
    public void getMarkets_shouldReturnBadRequest_whenSortColumnIsBlank() throws Exception {
        mockMvc.perform(get(MARKET_URL)
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
    @DisplayName("GET " + MARKET_URL + " - проверка контракта при непредвиденной ошибке")
    public void getMarkets_shouldReturnInternalError_whenServerError() throws Exception {
        doThrow(new RuntimeException()).when(internalMarketService).getMarkets(any());

        mockMvc.perform(get(MARKET_URL)
                        .param("page", "0")
                        .param("items", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("POST " + MARKET_URL + " - проверка контракта при валидных данных")
    public void createMarket_shouldReturnOk_whenAllDataIsValid() throws Exception {
        CreateMarketRequest request = new CreateMarketRequest(
                MarketName.WILDBERRIES,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(MARKET_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("POST " + MARKET_URL + " - проверка контракта при пустом name")
    public void createMarket_shouldReturnBadRequest_whenNameIsNull() throws Exception {
        CreateMarketRequest request = new CreateMarketRequest(
                null,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(MARKET_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`name` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + MARKET_URL + " - проверка контракта при пустом urlDomain")
    public void createMarket_shouldReturnBadRequest_whenUrlDomainIsNull() throws Exception {
        CreateMarketRequest request = new CreateMarketRequest(
                MarketName.WILDBERRIES,
                null
        );

        mockMvc.perform(post(MARKET_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`urlDomain` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + MARKET_URL + " - проверка контракта при пустом urlDomain")
    public void createMarket_shouldReturnBadRequest_whenUrlDomainIsBlank() throws Exception {
        CreateMarketRequest request = new CreateMarketRequest(
                MarketName.WILDBERRIES,
                " "
        );

        mockMvc.perform(post(MARKET_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`urlDomain` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + MARKET_URL + " - проверка контракта при непредвиденной ошибке")
    public void createMarket_shouldReturnInternalError_whenServerError() throws Exception {
        doThrow(new RuntimeException()).when(internalMarketService).createMarket(any());

        CreateMarketRequest request = new CreateMarketRequest(
                MarketName.WILDBERRIES,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(post(MARKET_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("PUT " + UPDATE_MARKET_URL + " - проверка контракта при валидных данных")
    public void updateMarket_shouldReturnOk_whenAllDataIsValid() throws Exception {
        Long marketId = RandomUtils.nextLong();
        UpdateMarketRequest request = new UpdateMarketRequest(
                MarketName.WILDBERRIES,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_MARKET_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("PUT " + UPDATE_MARKET_URL + " - проверка контракта при отрицательном marketId")
    public void updateMarket_shouldReturnBadRequest_whenRuleIdIsNegative() throws Exception {
        Long marketId = -RandomUtils.nextLong();
        UpdateMarketRequest request = new UpdateMarketRequest(
                MarketName.WILDBERRIES,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_MARKET_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`marketId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_MARKET_URL + " - проверка контракта при пустом name")
    public void updateMarket_shouldReturnBadRequest_whenNameIsNull() throws Exception {
        Long marketId = RandomUtils.nextLong();
        UpdateMarketRequest request = new UpdateMarketRequest(
                null,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_MARKET_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`name` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_MARKET_URL + " - проверка контракта при пустом urlDomain")
    public void updateMarket_shouldReturnBadRequest_whenUrlDomainIsNull() throws Exception {
        Long marketId = RandomUtils.nextLong();
        UpdateMarketRequest request = new UpdateMarketRequest(
                MarketName.WILDBERRIES,
                null
        );

        mockMvc.perform(put(UPDATE_MARKET_URL, marketId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`urlDomain` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_MARKET_URL + " - проверка контракта при пустом urlDomain")
    public void updateMarket_shouldReturnBadRequest_whenUrlDomainIsBlank() throws Exception {
        Long ruleId = RandomUtils.nextLong();
        UpdateMarketRequest request = new UpdateMarketRequest(
                MarketName.WILDBERRIES,
                " "
        );

        mockMvc.perform(put(UPDATE_MARKET_URL, ruleId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`urlDomain` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("PUT " + UPDATE_MARKET_URL + " - проверка контракта при непредвиденной ошибке")
    public void updateMarket_shouldReturnInternalError_whenServerError() throws Exception {
        doThrow(new RuntimeException()).when(internalMarketService).updateMarket(any(), any());

        UpdateMarketRequest request = new UpdateMarketRequest(
                MarketName.WILDBERRIES,
                RandomStringUtils.randomAlphabetic(5)
        );

        mockMvc.perform(put(UPDATE_MARKET_URL, RandomUtils.nextLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
