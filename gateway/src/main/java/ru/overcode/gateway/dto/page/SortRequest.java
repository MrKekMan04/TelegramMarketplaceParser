package ru.overcode.gateway.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;

public record SortRequest(

        @Schema(description = "Сортировочная колонка", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "`column` не может быть пустым")
        String column,

        @Schema(description = "Порядок сортировки", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "`order` не может быть пустым")
        Sort.Direction order
) {

}
