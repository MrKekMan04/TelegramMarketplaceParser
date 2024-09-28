package ru.overcode.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.overcode.gateway.dto.link.AddLinkRequest;
import ru.overcode.gateway.dto.link.AddLinkResponse;
import ru.overcode.gateway.dto.link.GetLinkResponse;
import ru.overcode.gateway.dto.link.RemoveLinkRequest;
import ru.overcode.gateway.service.LinkService;
import ru.overcode.shared.api.ListResponse;
import ru.overcode.shared.api.Response;

@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
@Validated
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка")
})
public class LinkController {

    private final LinkService linkService;

    @GetMapping
    @Operation(summary = "Получить список отслеживаемых ссылок")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отслеживаемые ссылки успешно получены"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - chatId - обязательный положительный параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка данных:
                        - чат не найден
                    """)
    })
    public ListResponse<GetLinkResponse> getLinks(
            @NotNull(message = "`chatId` не может быть пустым")
            @Positive(message = "`chatId` не может быть отрицательным") @RequestParam Long chatId
    ) {
        return ListResponse.success(linkService.getLinks(chatId));
    }

    @PostMapping
    @Operation(summary = "Добавить ссылку в отслеживаемые")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена в отслеживаемые"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - request.chatId - обязательный положительный параметр
                        - request.linkUrl - обязательный непустой параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка данных:
                        - чат не найден
                        - ссылка не поддерживается
                        - ссылка уже добавлена в отслеживаемые
                    """)
    })
    public Response<AddLinkResponse> addLink(
            @Valid @RequestBody AddLinkRequest request
    ) {
        return Response.success(linkService.addLink(request.chatId(), request.linkUrl()));
    }

    @DeleteMapping("/{linkId}")
    @Operation(summary = "Удалить ссылку из отслеживаемых")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ссылка успешно удалена из отслеживаемых"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - request.chatId - обязательный положительный параметр
                        - linkId - обязательный положительный параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка данных:
                        - чат не найден
                        - ссылка не найдена
                        - ссылка не отслеживается
                    """)
    })
    public Response<Void> removeLink(
            @Parameter(description = "Внутренний идентификатор ссылки")
            @NotNull(message = "`linkId` не может быть пустым")
            @Positive(message = "`linkId` не может быть отрицательным") @PathVariable Long linkId,
            @Valid @RequestBody RemoveLinkRequest request
    ) {
        linkService.removeLink(request.chatId(), linkId);
        return Response.success();
    }
}
