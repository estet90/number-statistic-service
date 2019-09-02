package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

public class ResponseWriter {

    private ResponseWriter() {
    }

    public static void writeOkResponse(Logger logger,
                                       String point,
                                       HttpExchange exchange,
                                       Supplier<String> responseBuilder) {
        writeResponse(logger, point, exchange, responseBuilder, HttpURLConnection.HTTP_OK);
    }

    public static void writeResponse(Logger logger,
                                     String point,
                                     HttpExchange exchange,
                                     int status) {
        try {
            exchange.getResponseHeaders().add("Content-type", "text/plain");
            exchange.sendResponseHeaders(status, -1);
            exchange.getResponseBody().close();
        } catch (Exception exception) {
            logger.error(point + ".thrown", exception);
            throw new RuntimeException("Ошибка при отправке ответа", exception);
        }
    }

    public static void writeClientErrorResponse(Logger logger,
                                                String point,
                                                HttpExchange exchange,
                                                Exception e,
                                                Function<Exception, String> errorResponseBuilder) {
        writeErrorResponse(logger, point, exchange, e, errorResponseBuilder, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public static void writeServerErrorResponse(Logger logger,
                                                String point,
                                                HttpExchange exchange,
                                                Exception e,
                                                Function<Exception, String> errorResponseBuilder) {
        writeErrorResponse(logger, point, exchange, e, errorResponseBuilder, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    private static void writeResponse(Logger logger,
                                      String point,
                                      HttpExchange exchange,
                                      Supplier<String> responseBuilder,
                                      int status) {
        try {
            exchange.getResponseHeaders().add("Content-type", "text/plain");
            var response = responseBuilder.get();
            if (nonNull(response)) {
                exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);
                writeResponseBody(exchange, response);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
                exchange.getResponseBody().close();
                logger.warn(point + ".warn метод вернул null");
            }
        } catch (Exception exception) {
            logger.error(point + ".thrown", exception);
            throw new RuntimeException("Ошибка при отправке ответа", exception);
        }
    }

    private static void writeResponseBody(HttpExchange exchange, String response) throws IOException {
        try (
                var outputStream = exchange.getResponseBody();
                var bodyStream = new ByteArrayOutputStream()
        ) {
            outputStream.write(response.getBytes());
            bodyStream.write(response.getBytes());
            exchange.setStreams(exchange.getRequestBody(), bodyStream);
        }
    }

    private static void writeErrorResponse(Logger logger,
                                           String point,
                                           HttpExchange exchange,
                                           Exception e,
                                           Function<Exception, String> errorResponseBuilder,
                                           int status) {
        writeResponse(logger, point, exchange, () -> errorResponseBuilder.apply(e), status);
    }

}
