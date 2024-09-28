package ru.overcode.gateway.exception;

import java.util.ArrayList;
import java.util.List;

public class BaseException extends RuntimeException {

    protected List<CodeWithMessage> exceptions = new ArrayList<>();

    public List<CodeWithMessage> getExceptions() {
        return exceptions;
    }
}
