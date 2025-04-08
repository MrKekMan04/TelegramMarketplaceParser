package ru.overcode.shared.api;

import java.util.List;

public class PageResponse<T> extends AbstractResponse<PageContent<T>> {

    private PageResponse() {
    }

    public static <T> PageResponse<T> success(Integer totalPagesCount, List<T> items) {
        PageResponse<T> response = new PageResponse<>();
        response.setData(new PageContent<>(totalPagesCount, items));
        return response;
    }
}
