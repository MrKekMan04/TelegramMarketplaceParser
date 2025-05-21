package ru.overcode.gateway.controller.link;

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
import ru.overcode.gateway.dto.link.AddLinkRequest;
import ru.overcode.gateway.dto.link.AddLinkResponse;
import ru.overcode.gateway.dto.link.GetLinkResponse;
import ru.overcode.gateway.dto.link.RemoveLinkRequest;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.exception.handler.RestExceptionHandler;
import ru.overcode.gateway.mapper.rest.ResponseMapper;
import ru.overcode.gateway.service.link.LinkService;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.overcode.gateway.util.TestUtils.getErrorPath;

@WebMvcTest({LinkController.class, RestExceptionHandler.class, ResponseMapper.class})
@AutoConfigureMockMvc
public class LinkControllerTest {

    private static final String LINK_URL = "/api/v1/links";
    private static final String REMOVE_LINK_URL = LINK_URL + "/{linkId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LinkService linkService;

    @AfterEach
    public void resetMocks() {
        Mockito.reset(linkService);
    }

    @Test
    @DisplayName("GET " + LINK_URL + " - проверка контракта при валидных данных")
    public void getLinks_shouldReturnOk_whenAllDataIsValid() throws Exception {
        final long chatId = 4123L;

        doReturn(List.of(
                new GetLinkResponse(414L, URI.create("http://localhost:8080"), List.of())
        )).when(linkService).getLinksWithRules(any());

        mockMvc.perform(get(LINK_URL)
                        .param("chatId", Long.toString(chatId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("GET " + LINK_URL + " - проверка контракта при отрицательном chatId")
    public void getLinks_shouldReturnBadRequest_whenChatIdIsNegative() throws Exception {
        final long chatId = -4123L;

        mockMvc.perform(get(LINK_URL)
                        .param("chatId", Long.toString(chatId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("GET " + LINK_URL + " - проверка контракта при не найденном чате")
    public void getLinks_shouldReturnUnprocessableEntity_whenChatNotFound() throws Exception {
        final long chatId = 4123L;

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.CHAT_NOT_FOUND
                .withParam("chatId", Long.toString(chatId))))
                .when(linkService).getLinksWithRules(any());

        mockMvc.perform(get(LINK_URL)
                        .param("chatId", Long.toString(chatId)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.CHAT_NOT_FOUND.getMessage()
                        .replace("{chatId}", Long.toString(chatId)))).exists());
    }

    @Test
    @DisplayName("GET " + LINK_URL + " - проверка контракта при непредвиденной ошибке")
    public void getLinks_shouldReturnInternalError_whenServerError() throws Exception {
        final long chatId = 4123L;

        doThrow(new RuntimeException())
                .when(linkService).getLinksWithRules(any());

        mockMvc.perform(get(LINK_URL)
                        .param("chatId", Long.toString(chatId)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.INTERNAL_SERVER.getMessage())).exists());
    }

    @Test
    @DisplayName("POST " + LINK_URL + " - проверка контракта при валидных данных")
    public void addLink_shouldReturnOk_whenAllDataIsValid() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(143L, URI.create("http://localhost:8080"));

        doReturn(new AddLinkResponse(412L))
                .when(linkService).addLink(any(), any());

        mockMvc.perform(post(LINK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("POST " + LINK_URL + " - проверка контракта при пустом chatId")
    public void addLink_shouldReturnBadRequest_whenChatIdIsNull() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(null, URI.create("http://localhost:8080"));

        mockMvc.perform(post(LINK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + LINK_URL + " - проверка контракта при отрицательном chatId")
    public void addLink_shouldReturnBadRequest_whenChatIdIsNegative() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(-143L, URI.create("http://localhost:8080"));

        mockMvc.perform(post(LINK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("POST " + LINK_URL + " - проверка контракта при пустом linkUrl")
    public void addLink_shouldReturnBadRequest_whenLinkUrlIsNull() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(153L, null);

        mockMvc.perform(post(LINK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`linkUrl` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("POST " + LINK_URL + " - проверка контракта при ненайденном чате")
    public void addLink_shouldReturnUnprocessableEntity_whenChatNotFound() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(143L, URI.create("http://localhost:8080"));

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.CHAT_NOT_FOUND
                .withParam("chatId", request.chatId().toString())))
                .when(linkService).addLink(any(), any());

        mockMvc.perform(post(LINK_URL)
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
    @DisplayName("POST " + LINK_URL + " - проверка контракта при неподдерживаемой ссылке")
    public void addLink_shouldReturnUnprocessableEntity_whenLinkNotSupported() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(143L, URI.create("http://localhost:8080"));

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_SUPPORTED
                .withParam("url", request.linkUrl().toString())))
                .when(linkService).addLink(any(), any());

        mockMvc.perform(post(LINK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.LINK_NOT_SUPPORTED.getMessage()
                        .replace("{url}", request.linkUrl().toString()))).exists());
    }

    @Test
    @DisplayName("POST " + LINK_URL + " - проверка контракта при уже отслеживаемой ссылке")
    public void addLink_shouldReturnUnprocessableEntity_whenLinkAlreadyAdded() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(143L, URI.create("http://localhost:8080"));

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_ALREADY_ADDED
                .withParam("url", request.linkUrl().toString())))
                .when(linkService).addLink(any(), any());

        mockMvc.perform(post(LINK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.LINK_ALREADY_ADDED.getMessage()
                        .replace("{url}", request.linkUrl().toString()))).exists());
    }

    @Test
    @DisplayName("POST " + LINK_URL + " - проверка контракта при непредвиденной ошибке")
    public void addLink_shouldReturnInternalError_whenServerError() throws Exception {
        final AddLinkRequest request = new AddLinkRequest(143L, URI.create("http://localhost:8080"));

        doThrow(new RuntimeException())
                .when(linkService).addLink(any(), any());

        mockMvc.perform(post(LINK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.INTERNAL_SERVER.getMessage())).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при валидных данных")
    public void removeLink_shouldReturnOk_whenAllDataIsValid() throws Exception {
        final Long linkId = 4153L;
        final RemoveLinkRequest request = new RemoveLinkRequest(143L);

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при отрицательном linkId")
    public void removeLink_shouldReturnBadRequest_whenLinkIdIsNegative() throws Exception {
        final Long linkId = -1245L;
        final RemoveLinkRequest request = new RemoveLinkRequest(143L);

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`linkId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при пустом chatId")
    public void removeLink_shouldReturnBadRequest_whenChatIdIsNull() throws Exception {
        final Long linkId = 51531L;
        final RemoveLinkRequest request = new RemoveLinkRequest(null);

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть пустым")).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при отрицательном chatId")
    public void removeLink_shouldReturnBadRequest_whenChatIdIsNegative() throws Exception {
        final Long linkId = 1245L;
        final RemoveLinkRequest request = new RemoveLinkRequest(-143L);

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath("`chatId` не может быть отрицательным")).exists());
    }

    @Test
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при ненайденном чате")
    public void removeLink_shouldReturnUnprocessableEntity_whenChatNotFound() throws Exception {
        final Long linkId = 4153L;
        final RemoveLinkRequest request = new RemoveLinkRequest(143L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.CHAT_NOT_FOUND
                .withParam("chatId", request.chatId().toString())))
                .when(linkService).removeLink(any(), any());

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
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
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при ненайденном чате")
    public void removeLink_shouldReturnUnprocessableEntity_whenLinkNotFound() throws Exception {
        final Long linkId = 4153L;
        final RemoveLinkRequest request = new RemoveLinkRequest(143L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_FOUND
                .withParam("linkId", linkId.toString())))
                .when(linkService).removeLink(any(), any());

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
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
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при неотслеживаемой ссылке")
    public void removeLink_shouldReturnUnprocessableEntity_whenLinkNotAdded() throws Exception {
        final Long linkId = 4153L;
        final RemoveLinkRequest request = new RemoveLinkRequest(143L);

        doThrow(new UnprocessableEntityException(GatewayExceptionMessage.LINK_NOT_ADDED
                .withParam("linkId", linkId.toString())))
                .when(linkService).removeLink(any(), any());

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
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
    @DisplayName("DELETE " + REMOVE_LINK_URL + " - проверка контракта при непредвиденной ошибке")
    public void removeLink_shouldReturnInternalError_whenServerError() throws Exception {
        final Long linkId = 4153L;
        final RemoveLinkRequest request = new RemoveLinkRequest(143L);

        doThrow(new RuntimeException())
                .when(linkService).removeLink(any(), any());

        mockMvc.perform(delete(REMOVE_LINK_URL, linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath(getErrorPath(GatewayExceptionMessage.INTERNAL_SERVER.getMessage())).exists());
    }
}
