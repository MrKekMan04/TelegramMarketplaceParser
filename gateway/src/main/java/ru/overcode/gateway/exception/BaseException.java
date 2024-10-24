package ru.overcode.gateway.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class BaseException extends RuntimeException {

    protected List<CodeWithMessage> exceptions = new ArrayList<>();
}
