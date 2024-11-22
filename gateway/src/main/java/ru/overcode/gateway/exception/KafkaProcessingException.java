package ru.overcode.gateway.exception;

public class KafkaProcessingException extends RuntimeException {

    public KafkaProcessingException(String entity, Object record, Throwable e) {
        super("Error was occurred while processing %s: %s".formatted(entity, record), e);
    }
}
