package ru.kononov.numberstatisticservice.api.server;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ru.kononov.numberstatisticservice.api.dto.Operation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
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
        try (var bodyStream = new PushbackInputStream(exchange.getRequestBody(), 1024)) {
            var byteStream = new ByteArrayOutputStream();
            IOUtils.copy(bodyStream, byteStream);
            var body = byteStream.toString(StandardCharsets.UTF_8.name());
            bodyStream.unread(byteStream.toByteArray());
            exchange.setStreams(bodyStream, exchange.getResponseBody());
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
        logger.info(
                "{}.request\n\tmethod={}\n\turi={}\n\theaders={}\n\tbody={}",
                operation.name(),
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders(),
                body
        );
    }

    private void logIn(HttpExchange exchange) {
        logger.info(
                "{}.request\n\tmethod={}\n\turi={}\n\theaders={}",
                operation.name(),
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders()
        );
    }

    private void logResponse(HttpExchange exchange) throws IOException {
        try (var bodyStream = exchange.getResponseBody()) {
            if (bodyStream instanceof ByteArrayOutputStream) {
                var payload = ((ByteArrayOutputStream) bodyStream).toString(StandardCharsets.UTF_8.name());
                logOut(exchange, payload);
            } else {
                logOut(exchange);
            }
        }
    }

    private void logOut(HttpExchange exchange, String body) {
        logger.info(
                "{}.response\n\tstatus={}\n\theaders={}\n\tbody={}",
                operation.name(),
                exchange.getResponseCode(),
                exchange.getResponseHeaders(),
                body
        );
    }

    private void logOut(HttpExchange exchange) {
        logger.info(
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
