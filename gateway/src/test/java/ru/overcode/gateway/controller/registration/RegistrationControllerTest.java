package ru.overcode.gateway.controller.registration;

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
import ru.overcode.gateway.dto.registration.RegistrationChatRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.exception.handler.RestExceptionHandler;
import ru.overcode.gateway.mapper.rest.ResponseMapper;
import ru.overcode.gateway.service.registration.RegistrationService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.overcode.gateway.util.TestUtils.getErrorPath;

@WebMvcTest({RegistrationController.class, RestExceptionHandler.class, ResponseMapper.class})
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    private static final String REGISTRATION_URL = "/api/v1/registration";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    @AfterEach
    public void resetMocks() {
        Mockito.reset(registrationService);
    }

    @Test
    @DisplayName("POST " + REGISTRATION_URL + " - проверка контракта при валидных данных")
    public void registerChat_shouldReturnOk_whenAllDataIsValid() throws Exception {
        final RegistrationChatRequest request = new RegistrationChatRequest(1L);

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("POST " + REGISTRATION_URL + " - проверка контракта при пустом chatId")
    public void registerChat_shouldReturnBadRequest_whenChatIdIsNull() throws Exception {
        final RegistrationChatRequest request = new RegistrationChatRequest(null);

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + REGISTRATION_URL + " - проверка контракта при отрицательном chatId")
    public void registerChat_shouldReturnBadRequest_whenChatIdIsNegative() throws Exception {
        final RegistrationChatRequest request = new RegistrationChatRequest(-1L);

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + REGISTRATION_URL + " - проверка контракта при уже зарегистрированном чате")
    public void registerChat_shouldReturnUnprocessableEntity_whenChatIsAlreadyRegistered() throws Exception {
        final RegistrationChatRequest request = new RegistrationChatRequest(1L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.CHAT_ALREADY_REGISTERED))
                .when(registrationService).registerChat(any());

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.CHAT_ALREADY_REGISTERED.getMessage()))
                        .exists());
    }

    @Test
    @DisplayName("POST " + REGISTRATION_URL + " - проверка контракта при непредвиденной ошибке")
    public void registerChat_shouldReturnInternalError_whenServerError() throws Exception {
        final RegistrationChatRequest request = new RegistrationChatRequest(1L);

        doThrow(new RuntimeException())
                .when(registrationService).registerChat(any());

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.INTERNAL_SERVER.getMessage())).exists());
    }
}
