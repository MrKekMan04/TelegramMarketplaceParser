package ru.overcode.gateway.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

    public static String getErrorPath(String message) {
        return "$.errors[?(@.message == \"" + message + "\")]";
    }
}
