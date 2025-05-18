package ru.overcode.gateway.controller.marketrule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.overcode.gateway.dto.market.BindRuleRequest;
import ru.overcode.gateway.service.marketrule.InternalMarketRuleService;
import ru.overcode.shared.api.Response;

@RestController
@RequestMapping("/internal/api/v1/markets/{marketId}/rules")
@RequiredArgsConstructor
@Validated
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка")
})
public class InternalMarketRuleController {

    private final InternalMarketRuleService internalMarketRuleService;

    @PostMapping
    @Operation(summary = "Привязать маркетплейс к правилу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Маркетплейс успешно привязан к правилу"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - marketId - обязательный непустой неотрицательный параметр
                        - request.id - обязательный непустой неотрицательный параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка процессинга:
                        - маркетплейс и правило уже привязаны
                    """)
    })
    public Response<Void> bindRule(
            @NotNull(message = "`marketId` не может быть пустым")
            @Positive(message = "`marketId` не может быть отрицательным")
            @PathVariable Long marketId,
            @RequestBody @Valid BindRuleRequest request
    ) {
        internalMarketRuleService.bindRule(marketId, request);
        return Response.success();
    }
}
