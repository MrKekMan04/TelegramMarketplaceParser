package ru.overcode.gateway.mapper;

import org.springframework.stereotype.Component;
import ru.overcode.gateway.exception.CodeWithMessage;
import ru.overcode.shared.api.ErrorDto;

@Component
public class ResponseMapper {

    public ErrorDto toError(CodeWithMessage error) {
        return new ErrorDto(error.getCode(), error.getMessage());
    }
}
