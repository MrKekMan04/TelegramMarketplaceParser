package ru.overcode.shared.api;

import java.util.List;

public class Response<T> extends AbstractResponse<T> {

    private Response() {
    }

    public static <T> Response<T> success() {
        return success(null);
    }

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setData(data);
        return response;
    }

    public static <T> Response<T> fail(List<ErrorDto> errors) {
        Response<T> response = new Response<>();
        response.setErrors(errors);
        return response;
    }
}
