package ru.overcode.shared.api;

import java.util.List;

public class ListResponse<T> extends AbstractResponse<List<T>> {

    private ListResponse() {
    }

    public static <T> ListResponse<T> success() {
        return success(null);
    }

    public static <T> ListResponse<T> success(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.setData(data);
        return response;
    }

    public ListResponse<T> fail(List<ErrorDto> errors) {
        ListResponse<T> response = new ListResponse<>();
        response.setErrors(errors);
        return response;
    }
}
