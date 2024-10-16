package ru.overcode.gateway.controller.rule;

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
import ru.overcode.gateway.dto.rule.AddRuleRequest;
import ru.overcode.gateway.dto.rule.RemoveRuleRequest;
import ru.overcode.gateway.dto.rule.RuleDto;
import ru.overcode.gateway.service.rule.RuleService;
import ru.overcode.shared.api.Response;

@RestController
@RequestMapping("/api/v1/links/{linkId}/rules")
@RequiredArgsConstructor
@Validated
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка")
})
public class RuleController {

    private final RuleService ruleService;

    @PostMapping
    @Operation(summary = "Добавить правило отслеживания для отслеживаемой ссылки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Правило для ссылки успешно добавлено"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - linkId - обязательный положительный параметр
                        - request.chatId - обязательный положительный параметр
                        - request.ruleId - обязательный положительный параметр
                        - request.ruleParams - обязательный параметр размером максимум 50 элементов
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка данных:
                        - чат не найден
                        - ссылка не найдена
                        - ссылка не отслеживается
                        - правило не найдено
                        - правило уже добавлено
                        - некорректные параметры
                    """)
    })
    public Response<RuleDto> addRule(
            @Parameter(description = "Внутренний идентификатор ссылки")
            @NotNull(message = "`linkId` не может быть пустым")
            @Positive(message = "`linkId` не может быть отрицательным") @PathVariable Long linkId,
            @Valid @RequestBody AddRuleRequest request
    ) {
        return Response.success(ruleService.addRule(request.chatId(), linkId, request.ruleId(), request.ruleParams()));
    }

    @DeleteMapping("/{ruleId}")
    @Operation(summary = "Удалить правило отслеживания для отслеживаемой ссылки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Правило для ссылки успешно удалено"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - linkId - обязательный положительный параметр
                        - ruleId - обязательный положительный параметр
                        - request.chatId - обязательный положительный параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ощибка данных:
                        - чат не найден
                        - ссылка не найдена
                        - ссылка не отслеживается
                        - правило не найдено
                        - правило не отслеживается
                    """)
    })
    public Response<Void> removeRule(
            @Parameter(description = "Внутренний идентификатор ссылки")
            @NotNull(message = "`linkId` не может быть пустым")
            @Positive(message = "`linkId` не может быть отрицательным") @PathVariable Long linkId,
            @Parameter(description = "Внутренний идентификатор правила")
            @NotNull(message = "`ruleId` не может быть пустым")
            @Positive(message = "`ruleId` не может быть отрицательным") @PathVariable Long ruleId,
            @Valid @RequestBody RemoveRuleRequest request
    ) {
        ruleService.removeRule(request.chatId(), linkId, ruleId);
        return Response.success();
    }
}
