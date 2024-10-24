package ru.overcode.gateway.exception.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.overcode.gateway.exception.GatewayExceptionMessage;
import ru.overcode.gateway.exception.UnprocessableEntityException;
import ru.overcode.gateway.mapper.rest.ResponseMapper;
import ru.overcode.shared.api.Response;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

    private final ResponseMapper responseMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception ignored) {
        return ResponseEntity.internalServerError()
                .body(Response.fail(List.of(responseMapper.toError(GatewayExceptionMessage.INTERNAL_SERVER))));
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Response<Void>> handleUnprocessableEntityException(UnprocessableEntityException e) {
        return ResponseEntity.unprocessableEntity()
                .body(Response.fail(e.getExceptions().stream()
                        .map(responseMapper::toError)
                        .toList()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                .body(Response.fail(e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .map(exceptionMessage -> responseMapper.toError(GatewayExceptionMessage.BAD_REQUEST
                                .withParam("param", exceptionMessage)))
                        .toList()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        return ResponseEntity.badRequest()
                .body(Response.fail(e.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .map(exceptionMessage -> responseMapper.toError(GatewayExceptionMessage.BAD_REQUEST
                                .withParam("param", exceptionMessage)))
                        .toList()));
    }
}
