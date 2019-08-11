package ru.kononov.numberstatisticservice.api;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kononov.numberstatisticservice.api.handler.AddNumberHandler;
import ru.kononov.numberstatisticservice.api.handler.AverageNumberHandler;
import ru.kononov.numberstatisticservice.api.handler.MaxNumberHandler;
import ru.kononov.numberstatisticservice.api.handler.MinNumberHandler;
import ru.kononov.numberstatisticservice.api.util.PropertyResolver;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;

class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private final AddNumberHandler addNumberHandler;
    private final AverageNumberHandler averageNumberHandler;
    private final MaxNumberHandler maxNumberHandler;
    private final MinNumberHandler minNumberHandler;
    private final PropertyResolver propertyResolver;

    Server(NumberStorage numberStorage, String propertyFileName) {
        this.addNumberHandler = new AddNumberHandler(numberStorage);
        this.averageNumberHandler = new AverageNumberHandler(numberStorage);
        this.maxNumberHandler = new MaxNumberHandler(numberStorage);
        this.minNumberHandler = new MinNumberHandler(numberStorage);
        this.propertyResolver = new PropertyResolver(propertyFileName);
    }

    void start() throws IOException {
        var start = LocalDateTime.now();
        var inetSocketAddress = new InetSocketAddress(propertyResolver.getPort());
        var server = HttpServer.create(inetSocketAddress, 0);
        var executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        addHandlers(server);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Server is shutting down...");
            server.stop(0);
        }));
        server.start();
        logger.info("Server started at {} millis", start.until(LocalDateTime.now(), ChronoUnit.MILLIS));
    }

    private void addHandlers(HttpServer server) {
        var contextPath = propertyResolver.getContextPath();
        server.createContext(contextPath + "/numbers", addNumberHandler);
        server.createContext(contextPath + "/numbers/average", averageNumberHandler);
        server.createContext(contextPath + "/numbers/max", maxNumberHandler);
        server.createContext(contextPath + "/numbers/min", minNumberHandler);
    }

}
