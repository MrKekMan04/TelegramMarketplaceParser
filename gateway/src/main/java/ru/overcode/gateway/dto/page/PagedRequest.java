package ru.overcode.gateway.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PagedRequest(

        @Schema(description = "Номер страницы, начиная с 0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`page` не может быть пустым")
        @PositiveOrZero(message = "`page` должен быть больше или равен 0")
        Integer page,

        @Schema(description = "Количество элементов на странице", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`items` не может быть пустым")
        @Min(value = 1, message = "`items` должен быть больше или равен {value}")
        @Max(value = 500, message = "`items` должен быть меньше лии равен {value}")
        Integer items,

        @Schema(description = "Объект сортировки", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        SortRequest sort
) {

}
