package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Supplier;

public class ResponseWriter {

    private ResponseWriter() {
    }

    public static String writeResponse(Logger logger,
                                       String point,
                                       HttpExchange exchange,
                                       Supplier<String> responseBuilder,
                                       int status) {
        try {
            var response = responseBuilder.get();
            exchange.getResponseHeaders().add("Content-type", "text/plain");
            exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);
            try (var outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
            return response;
        } catch (Exception exception) {
            logger.error(point + ".thrown", exception);
            throw new RuntimeException("Ошибка при отправке ответа", exception);
        }
    }

    public static void writeResponse(Logger logger,
                                     String point,
                                     HttpExchange exchange,
                                     int status) {
        try {
            exchange.getResponseHeaders().add("Content-type", "text/plain");
            exchange.sendResponseHeaders(status, 0);
        } catch (Exception exception) {
            logger.error(point + ".thrown", exception);
            throw new RuntimeException("Ошибка при отправке ответа", exception);
        }
    }

    static String writeErrorResponse(Logger logger,
                                     String point,
                                     HttpExchange exchange,
                                     Function<String, String> errorResponseBuilder,
                                     Exception e,
                                     int status) {
        return writeResponse(logger, point, exchange, () -> errorResponseBuilder.apply(e.getMessage()), status);
    }

}
