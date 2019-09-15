package ru.kononov.numberstatisticservice.api.server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kononov.numberstatisticservice.api.dto.Operation;
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

import static ru.kononov.numberstatisticservice.api.dto.Operation.*;

public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private final AddNumberHandler addNumberHandler;
    private final AverageNumberHandler averageNumberHandler;
    private final MaxNumberHandler maxNumberHandler;
    private final MinNumberHandler minNumberHandler;
    private final PropertyResolver propertyResolver;

    public Server(NumberStorage numberStorage, String propertyFileName) {
        this.addNumberHandler = new AddNumberHandler(numberStorage);
        this.averageNumberHandler = new AverageNumberHandler(numberStorage);
        this.maxNumberHandler = new MaxNumberHandler(numberStorage);
        this.minNumberHandler = new MinNumberHandler(numberStorage);
        this.propertyResolver = new PropertyResolver(propertyFileName);
    }

    public void start() throws IOException {
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
        createContextWithFilter(server, contextPath + "/numbers", addNumberHandler, add);
        createContextWithFilter(server, contextPath + "/numbers/average", averageNumberHandler, average);
        createContextWithFilter(server, contextPath + "/numbers/max", maxNumberHandler, max);
        createContextWithFilter(server, contextPath + "/numbers/min", minNumberHandler, min);
    }

    private void createContextWithFilter(HttpServer server, String path, HttpHandler handler, Operation operation) {
        var context = server.createContext(path, handler);
        context.getFilters().add(new LoggingFilter(operation));
    }

}
