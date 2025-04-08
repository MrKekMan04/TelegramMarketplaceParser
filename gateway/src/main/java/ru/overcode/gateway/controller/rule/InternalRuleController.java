package ru.overcode.gateway.controller.rule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.overcode.gateway.dto.page.PagedRequest;
import ru.overcode.gateway.dto.rule.internal.CreateRuleRequest;
import ru.overcode.gateway.dto.rule.internal.InternalRuleDto;
import ru.overcode.gateway.dto.rule.internal.UpdateRuleRequest;
import ru.overcode.gateway.service.rule.InternalRuleService;
import ru.overcode.shared.api.PageContent;
import ru.overcode.shared.api.PageResponse;
import ru.overcode.shared.api.Response;

@RestController
@RequestMapping("/internal/api/v1/rules")
@RequiredArgsConstructor
@Validated
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка")
})
public class InternalRuleController {

    private final InternalRuleService internalRuleService;

    @GetMapping
    @Operation(summary = "Получить существующие правила")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Правила успешно получены"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - page - обязательный параметр больше или равен 0
                        - items - обязательный параметр больше или равен 1 и меньше или равен 500
                        - sort.column - обязательный непустой параметр
                        - sort.order - обязательный непустой параметр
                    """)
    })
    public PageResponse<InternalRuleDto> getRules(@Valid PagedRequest request) {
        PageContent<InternalRuleDto> response = internalRuleService.getRules(request);
        return PageResponse.success(response.totalPages(), response.content());
    }

    @PostMapping
    @Operation(summary = "Создать новое правило")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Правило успешно создано"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - id - обязательный положительный параметр
                        - name - обязательный непустой параметр
                        - description - обязательный непустой параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка процессинга:
                        - правило уже создано
                    """)
    })
    public Response<InternalRuleDto> createRule(@RequestBody @Valid CreateRuleRequest request) {
        internalRuleService.createRule(request);
        return Response.success();
    }

    @PutMapping("/{ruleId}")
    @Operation(summary = "Изменить существующее правило")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Правило успешно создано"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - id - обязательный положительный параметр
                        - name - обязательный непустой параметр
                        - description - обязательный непустой параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка процессинга:
                        - правила не существует
                    """)
    })
    public Response<InternalRuleDto> updateRule(
            @NotNull(message = "`ruleId` не может быть пустым")
            @Positive(message = "`ruleId` не может быть отрицательным")
            @PathVariable Long ruleId,
            @RequestBody @Valid UpdateRuleRequest request
    ) {
        internalRuleService.updateRule(ruleId, request);
        return Response.success();
    }
}
