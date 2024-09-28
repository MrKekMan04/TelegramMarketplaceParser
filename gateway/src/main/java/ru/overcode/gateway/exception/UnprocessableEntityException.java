package ru.overcode.gateway.exception;

public class UnprocessableEntityException extends BaseException {

    public UnprocessableEntityException(GatewayExceptionMessage message) {
        exceptions.add(message);
    }
}
