package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.io.IOException;

public class MaxNumberHandler implements HttpHandler {

    private final NumberStorage numberStorage;

    public MaxNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
