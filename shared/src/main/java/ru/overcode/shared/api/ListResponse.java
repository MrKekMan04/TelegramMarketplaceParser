package ru.overcode.shared.api;

import java.util.List;

public class ListResponse<T> extends AbstractResponse<List<T>> {

    private ListResponse() {
    }

    public static <T> ListResponse<T> success(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.setData(data);
        return response;
    }
}
