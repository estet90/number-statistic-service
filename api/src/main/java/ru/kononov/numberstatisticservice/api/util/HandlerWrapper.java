package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandlerWrapper {

    private HandlerWrapper() {
    }

    public static void wrap(Logger logger,
                            String point,
                            HttpExchange exchange,
                            Function<HttpExchange, String> handler,
                            Function<String, String> errorResponseBuilder) {
        try {
            logger.info(createLogInString(point, exchange));
            var result = handler.apply(exchange);
            logger.info(createLogOutSuccessString(point, exchange, result));
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            var result = writeErrorResponse(logger, point, exchange, errorResponseBuilder, e, 400);
            logger.log(Level.WARNING, createLogOutErrorString(point, exchange, result), e);
        } catch (Exception e) {
            var result = writeErrorResponse(logger, point, exchange, errorResponseBuilder, e, 500);
            logger.log(Level.WARNING, createLogOutErrorString(point, exchange, result), e);
        }
    }

    public static String writeResponse(Logger logger,
                                       String point,
                                       HttpExchange exchange,
                                       Supplier<String> responseBuilder,
                                       int status) {
        try {
            var response = responseBuilder.get();
            exchange.sendResponseHeaders(status, response.length());
            try (var outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
            return response;
        } catch (IOException io) {
            logger.log(Level.WARNING, point + ".thrown", io);
            return null;
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
