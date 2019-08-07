package ru.kononov.numberstatisticservice.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kononov.numberstatisticservice.inmemorystorage.logic.ImMemoryNumberStorage;

import java.io.IOException;
import java.io.OutputStream;

public class Application {

    public static void main(String[] args) throws Exception {
        var storage = new ImMemoryNumberStorage();
        var server = new Server(storage);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
