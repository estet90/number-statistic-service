package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;

public class HttpMethodChecker {

    private HttpMethodChecker() {
    }

    public static void checkMethod(HttpExchange exchange, String expectedMethod) {
        var method = exchange.getRequestMethod();
        if (!expectedMethod.equals(method)) {
            throw new UnsupportedOperationException(String.format("Метод %s не поддерживается", method));
        }
    }

}
