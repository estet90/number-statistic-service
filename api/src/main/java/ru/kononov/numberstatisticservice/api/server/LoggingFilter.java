package ru.kononov.numberstatisticservice.api.server;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ru.kononov.numberstatisticservice.api.dto.Operation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class LoggingFilter extends Filter {

    private final Operation operation;
    private static final Logger logger = LogManager.getLogger(LoggingFilter.class);

    LoggingFilter(Operation operation) {
        this.operation = operation;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        threadContextInit(operation);
        try {
            logRequest(exchange, chain);
            logResponse(exchange);
        } finally {
            ThreadContext.clearAll();
        }
    }

    private void logRequest(HttpExchange exchange, Chain chain) throws IOException {
        try (var inputStream = exchange.getRequestBody()) {
            var byteStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, byteStream);
            var body = byteStream.toString(StandardCharsets.UTF_8.name());
            exchange.setStreams(new ByteArrayInputStream(byteStream.toByteArray()), exchange.getResponseBody());
            if (nonNull(body) && body.length() > 0) {
                logIn(exchange, body);
            } else {
                logIn(exchange);
            }
            chain.doFilter(exchange);
        } catch (Exception e) {
            logger.error("error", e);
            throw e;
        }
    }

    private void logIn(HttpExchange exchange, String body) {
        logger.debug(
                "{}.request\n\tmethod={}\n\turi={}\n\theaders={}\n\tbody={}",
                operation.name(),
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders(),
                body
        );
    }

    private void logIn(HttpExchange exchange) {
        logger.debug(
                "{}.request\n\tmethod={}\n\turi={}\n\theaders={}",
                operation.name(),
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders()
        );
    }

    private void logResponse(HttpExchange exchange) throws IOException {
        try (var bodyStream = exchange.getResponseBody()) {
            if (bodyStream instanceof ByteArrayOutputStream && ((ByteArrayOutputStream) bodyStream).size() > 0) {
                var body = ((ByteArrayOutputStream) bodyStream).toString(StandardCharsets.UTF_8.name());
                logOut(exchange, body);
            } else {
                logOut(exchange);
            }
        }
    }

    private void logOut(HttpExchange exchange, String body) {
        logger.debug(
                "{}.response\n\tstatus={}\n\theaders={}\n\tbody={}",
                operation.name(),
                exchange.getResponseCode(),
                exchange.getResponseHeaders(),
                body
        );
    }

    private void logOut(HttpExchange exchange) {
        logger.debug(
                "{}.response\n\tstatus={}\n\theaders={}",
                operation.name(),
                exchange.getResponseCode(),
                exchange.getResponseHeaders()
        );
    }

    private void threadContextInit(Operation operation) {
        Objects.requireNonNull(operation);
        ThreadContext.put("traceId", UUID.randomUUID().toString());
        ThreadContext.put("operationName", operation.name());
    }

    @Override
    public String description() {
        return "LoggingFilter";
    }

}
