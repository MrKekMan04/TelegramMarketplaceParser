package ru.overcode.shared.api;

import java.util.List;

public record PageContent<T>(
        Integer totalPages,
        List<T> content
) {

}
