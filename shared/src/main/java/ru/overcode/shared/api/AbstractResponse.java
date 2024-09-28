package ru.overcode.shared.api;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResponse<T> {

    private T data;
    private List<ErrorDto> errors = new ArrayList<>();

    public T getData() {
        return data;
    }

    public List<ErrorDto> getErrors() {
        return errors;
    }

    protected void setData(T data) {
        this.data = data;
    }

    protected void setErrors(List<ErrorDto> errors) {
        this.errors = errors;
    }
}
