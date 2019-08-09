package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ru.kononov.numberstatisticservice.api.dto.Operation;

import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static ru.kononov.numberstatisticservice.api.util.ResponseWriter.writeErrorResponse;

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
            threadContextInit(operation);
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

    public static void wrap(Logger logger,
                            String point,
                            Operation operation,
                            HttpExchange exchange,
                            Consumer<HttpExchange> handler,
                            Function<String, String> errorResponseBuilder) {
        try {
            threadContextInit(operation);
            logger.info(createLogInString(point, exchange));
            handler.accept(exchange);
            logger.info(createLogOutSuccessString(point, exchange, null));
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

    private static void threadContextInit(Operation operation) {
        Objects.requireNonNull(operation);
        ThreadContext.put("traceId", UUID.randomUUID().toString());
        ThreadContext.put("operationName", operation.name());
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
