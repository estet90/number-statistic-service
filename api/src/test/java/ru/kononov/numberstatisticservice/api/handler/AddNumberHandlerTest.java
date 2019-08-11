package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import ru.kononov.numberstatisticservice.inmemorystorage.logic.ImMemoryNumberStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;

import static org.mockito.Mockito.*;

class AddNumberHandlerTest {

    private final AddNumberHandler handler = new AddNumberHandler(new ImMemoryNumberStorage());

    @Test
    void handle() throws IOException {
        var exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("1".getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());

        handler.handle(exchange);

        verify(exchange, times(2)).getRequestMethod();
        verify(exchange).getRequestBody();
        verify(exchange, times(2)).getResponseHeaders();
        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_ACCEPTED), eq(-1L));
        verify(exchange).getResponseBody();
    }

    @Test
    void handleUnsupportedMethod() throws IOException {
        var exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("1".getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());

        handler.handle(exchange);

        verify(exchange, times(2)).getRequestMethod();
        verify(exchange, never()).getRequestBody();
        verify(exchange, times(2)).getResponseHeaders();
        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_BAD_REQUEST), any(long.class));
        verify(exchange).getResponseBody();
    }

    @Test
    void handleIncorrectValue() throws IOException {
        var exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("qwe".getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());

        handler.handle(exchange);

        verify(exchange, times(2)).getRequestMethod();
        verify(exchange).getRequestBody();
        verify(exchange, times(2)).getResponseHeaders();
        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_BAD_REQUEST), any(long.class));
        verify(exchange).getResponseBody();
    }

    @Test
    void handleNullValue() throws IOException {
        var exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(null);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());

        handler.handle(exchange);

        verify(exchange, times(2)).getRequestMethod();
        verify(exchange).getRequestBody();
        verify(exchange, times(2)).getResponseHeaders();
        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_BAD_REQUEST), any(long.class));
        verify(exchange).getResponseBody();
    }

    @Test
    void handleServerError() throws IOException {
        var storage = mock(ImMemoryNumberStorage.class);
        doThrow(RuntimeException.class).when(storage).add(any());
        var handler = new AddNumberHandler(storage);
        var exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("1".getBytes()));
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream(0));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());

        handler.handle(exchange);

        verify(exchange, times(2)).getRequestMethod();
        verify(exchange).getRequestBody();
        verify(exchange, times(2)).getResponseHeaders();
        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_INTERNAL_ERROR), any(long.class));
        verify(exchange).getResponseBody();
        verify(storage).add(eq(new BigDecimal("1")));
    }

}
