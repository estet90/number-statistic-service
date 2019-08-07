package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.io.IOException;
import java.math.BigDecimal;

public class AddNumberHandler implements HttpHandler {

    private final NumberStorage numberStorage;

    public AddNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            var path = exchange.getRequestURI().getRawPath();
            var fragments = path.split("/");
            var
            numberStorage.add();
        } else {
            System.out.println("GET!");
        }
    }

}
