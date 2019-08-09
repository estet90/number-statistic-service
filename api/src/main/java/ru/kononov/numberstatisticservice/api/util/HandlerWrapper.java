package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ru.kononov.numberstatisticservice.api.dto.Operation;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class HandlerWrapper {

    private HandlerWrapper() {
    }

    public static void wrap(Logger logger,
                            String point,
                            Operation operation,
                            HttpExchange exchange,
                            Function<HttpExchange, String> handler,
                            Function<String, String> errorResponseBuilder) {
        try {
            Objects.requireNonNull(operation);
            ThreadContext.put("traceId", UUID.randomUUID().toString());
            ThreadContext.put("operationName", operation.name());
            logger.info(createLogInString(point, exchange));
            var result = handler.apply(exchange);
            logger.info(createLogOutSuccessString(point, exchange, result));
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            var result = writeErrorResponse(logger, point, exchange, errorResponseBuilder, e, HttpURLConnection.HTTP_BAD_REQUEST);
            logger.error(createLogOutErrorString(point, exchange, result), e);
        } catch (Exception e) {
            var result = writeErrorResponse(logger, point, exchange, errorResponseBuilder, e, HttpURLConnection.HTTP_INTERNAL_ERROR);
            logger.error(createLogOutErrorString(point, exchange, result), e);
        } finally {
            ThreadContext.clearAll();
        }
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

    private static String writeErrorResponse(Logger logger,
                                             String point,
                                             HttpExchange exchange,
                                             Function<String, String> errorResponseBuilder,
                                             Exception e,
                                             int status) {
        return writeResponse(logger, point, exchange, () -> errorResponseBuilder.apply(e.getMessage()), status);
    }

    private static String createLogInString(String point, HttpExchange exchange) {
        return String.format("%s.in\n\tmethod=%s\n\turi=%s", point, exchange.getRequestMethod(), exchange.getRequestURI());
    }

    private static String createLogOutSuccessString(String point, HttpExchange exchange, String result) {
        return createLogOutString(point, "out", exchange, result);
    }

    private static String createLogOutErrorString(String point, HttpExchange exchange, String result) {
        return createLogOutString(point, "out.thrown", exchange, result);
    }

    private static String createLogOutString(String point, String label, HttpExchange exchange, String result) {
        return String.format(
                "%s.%s\n\tstatus=%s\n\theaders=%s\n\tpayload=%s",
                point,
                label,
                String.valueOf(exchange.getResponseCode()),
                exchange.getResponseHeaders().entrySet(),
                result
        );
    }

}
