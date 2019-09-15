package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

public class RequestReader {

    private RequestReader() {
    }

    public static String extractPayload(HttpExchange exchange) {
        try (var inputStream = requireNonNull(exchange.getRequestBody())) {
            var result = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, result);
            return result.toString(StandardCharsets.UTF_8.name());
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Не удалось извлечь тело запроса", e);
        }
    }

}
