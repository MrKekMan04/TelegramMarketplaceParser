package ru.overcode.gateway.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public enum GatewayExceptionMessage implements CodeWithMessage {

    BAD_REQUEST("{param}"),
    INTERNAL_SERVER("Произошла непредвиденная ошибка"),
    CHAT_NOT_FOUND("Чат с id {chatId} не найден"),
    CHAT_ALREADY_REGISTERED("Чат уже зарегистрирован"),
    LINK_NOT_FOUND("Ссылка с id {linkId} не найдена"),
    LINK_NOT_ADDED("Ссылка с id {linkId} не отслеживается"),
    LINK_ALREADY_ADDED("[Ссылка] ({url}) уже отслеживается"),
    LINK_NOT_SUPPORTED("[Ссылка] ({url}) не поддерживается"),
    RULE_NOT_FOUND("Правило с id {ruleId} не найдено"),
    RULE_ALREADY_ADDED("Правило с id {ruleId} уже отслеживается"),
    RULE_NOT_ADDED("Правило с id {ruleId} не отслеживается"),
    RULE_BAD_PARAMS("""
    Некорректные параметры для правила с id {ruleId}.
    Ожидаемые параметры: {expectedParams}.
    Переданные параметры: {actualParams}
    """);

    private final String message;
    private final Map<String, String> params = new HashMap<>();

    GatewayExceptionMessage(String message) {
        this.message = message;
    }

    public GatewayExceptionMessage withParam(String param, String value) {
        this.params.put(param, value);
        return this;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        AtomicReference<String> finalMessage = new AtomicReference<>(this.message);
        params.forEach((param, value) -> finalMessage.set(finalMessage.get().replace("{" + param + "}", value)));
        return finalMessage.get();
    }
}
