package ru.overcode.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.overcode.gateway.dto.registration.RegistrationChatRequest;
import ru.overcode.gateway.service.RegistrationService;
import ru.overcode.shared.api.Response;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка")
})
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чат успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - chatId - обязательный положительный параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка данных:
                        - чат уже зарегистрирован
                    """),
    })
    public Response<Void> registerChat(
            @Valid @RequestBody RegistrationChatRequest request
    ) {
        registrationService.registerChat(request.chatId());
        return Response.success();
    }
}
