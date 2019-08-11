package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import ru.kononov.numberstatisticservice.inmemorystorage.logic.ImMemoryNumberStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AverageNumberHandlerTest {

    @Test
    void handle() throws IOException {
        var storage = mock(ImMemoryNumberStorage.class);
        when(storage.average()).thenReturn(BigDecimal.ONE);
        var handler = new AverageNumberHandler(storage);
        var exchange = mock(HttpExchange.class);
        mockExchange(exchange, "GET");

        handler.handle(exchange);

        verifyResult(exchange, HttpURLConnection.HTTP_OK);
    }

    @Test
    void handleReturnNull() throws IOException {
        var storage = mock(ImMemoryNumberStorage.class);
        when(storage.average()).thenReturn(null);
        var handler = new AverageNumberHandler(storage);
        var exchange = mock(HttpExchange.class);
        mockExchange(exchange, "GET");

        handler.handle(exchange);

        verifyResult(exchange, HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Test
    void handleUnsupportedMethod() throws IOException {
        var handler = new AverageNumberHandler(new ImMemoryNumberStorage());
        var exchange = mock(HttpExchange.class);
        mockExchange(exchange, "POST");

        handler.handle(exchange);

        verifyResult(exchange, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    void handleServerError() throws IOException {
        var storage = mock(ImMemoryNumberStorage.class);
        when(storage.average()).thenThrow(RuntimeException.class);
        var handler = new AverageNumberHandler(storage);
        var exchange = mock(HttpExchange.class);
        mockExchange(exchange, "GET");

        handler.handle(exchange);

        verifyResult(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    private void mockExchange(HttpExchange exchange, String method) {
        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
    }

    private void verifyResult(HttpExchange exchange, int status) throws IOException {
        verify(exchange, times(2)).getRequestMethod();
        verify(exchange, times(2)).getResponseHeaders();
        verify(exchange).sendResponseHeaders(eq(status), any(long.class));
        verify(exchange).getResponseBody();
    }

}
