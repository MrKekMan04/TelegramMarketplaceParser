package ru.overcode.gateway.controller.market;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.overcode.gateway.dto.market.CreateMarketRequest;
import ru.overcode.gateway.dto.market.InternalMarketDto;
import ru.overcode.gateway.dto.market.MarketIdDto;
import ru.overcode.gateway.dto.market.UpdateMarketRequest;
import ru.overcode.gateway.dto.page.PagedRequest;
import ru.overcode.gateway.service.market.InternalMarketService;
import ru.overcode.shared.api.PageContent;
import ru.overcode.shared.api.PageResponse;
import ru.overcode.shared.api.Response;

@RestController
@RequestMapping("/internal/api/v1/markets")
@RequiredArgsConstructor
@Validated
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка")
})
public class InternalMarketController {

    private final InternalMarketService internalMarketService;

    @GetMapping
    @Operation(summary = "Получить существующие маркетплейсы")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Маркетплейсы успешно получены"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - page - обязательный параметр больше или равен 0
                        - items - обязательный параметр больше или равен 1 и меньше или равен 500
                        - sort.column - обязательный непустой параметр
                        - sort.order - обязательный непустой параметр
                    """)
    })
    public PageResponse<InternalMarketDto> getMarkets(@Valid PagedRequest request) {
        PageContent<InternalMarketDto> response = internalMarketService.getMarkets(request);
        return PageResponse.success(response.totalPages(), response.content());
    }

    @PostMapping
    @Operation(summary = "Создать новый маркетплейс")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Маркетплейс успешно создан"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - name - обязательный непустой параметр
                        - description - обязательный непустой параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка процессинга:
                        - маркетплейс уже создан
                    """)
    })
    public Response<MarketIdDto> createMarket(@RequestBody @Valid CreateMarketRequest request) {
        return Response.success(internalMarketService.createMarket(request));
    }

    @PutMapping("/{marketId}")
    @Operation(summary = "Изменить существующий маркетплейс")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Маркетплейс успешно изменен"),
            @ApiResponse(responseCode = "400", description = """
                    Ошибка валидации:
                        - marketId - обязательный непустой неотрицательный параметр
                        - name - обязательный непустой параметр
                        - description - обязательный непустой параметр
                    """),
            @ApiResponse(responseCode = "422", description = """
                    Ошибка процессинга:
                        - маркетплейс не существует
                    """)
    })
    public Response<Void> updateMarket(
            @NotNull(message = "`marketId` не может быть пустым")
            @Positive(message = "`marketId` не может быть отрицательным")
            @PathVariable Long marketId,
            @RequestBody @Valid UpdateMarketRequest request
    ) {
        internalMarketService.updateMarket(marketId, request);
        return Response.success();
    }
}
