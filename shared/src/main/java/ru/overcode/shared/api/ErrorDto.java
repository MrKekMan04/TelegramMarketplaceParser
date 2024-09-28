package ru.overcode.shared.api;

public record ErrorDto(
        String code,
        String message
) {
}
