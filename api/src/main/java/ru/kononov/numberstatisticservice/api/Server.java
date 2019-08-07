package ru.kononov.numberstatisticservice.api;

import com.sun.net.httpserver.HttpServer;
import ru.kononov.numberstatisticservice.api.handler.AddNumberHandler;
import ru.kononov.numberstatisticservice.api.handler.AverageNumberHandler;
import ru.kononov.numberstatisticservice.api.handler.MaxNumberHandler;
import ru.kononov.numberstatisticservice.api.handler.MinNumberHandler;
import ru.kononov.numberstatisticservice.api.util.PropertyResolver;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.io.IOException;
import java.net.InetSocketAddress;

class Server {

    private final AddNumberHandler addNumberHandler;
    private final AverageNumberHandler averageNumberHandler;
    private final MaxNumberHandler maxNumberHandler;
    private final MinNumberHandler minNumberHandler;
    private final PropertyResolver propertyResolver;

    Server(NumberStorage numberStorage) {
        this.addNumberHandler = new AddNumberHandler(numberStorage);
        this.averageNumberHandler = new AverageNumberHandler(numberStorage);
        this.maxNumberHandler = new MaxNumberHandler(numberStorage);
        this.minNumberHandler = new MinNumberHandler(numberStorage);
        this.propertyResolver = new PropertyResolver();
    }

    void start() throws IOException {
        var inetSocketAddress = new InetSocketAddress(propertyResolver.getPort());
        var server = HttpServer.create(inetSocketAddress, 0);
        var contextPath = propertyResolver.getContextPath();
        server.createContext(contextPath + "/numbers", addNumberHandler);
        server.createContext(contextPath + "/numbers/average", averageNumberHandler);
        server.createContext(contextPath + "/numbers/max", maxNumberHandler);
        server.createContext(contextPath + "/numbers/min", minNumberHandler);
        server.setExecutor(null);
        server.start();
    }

}
