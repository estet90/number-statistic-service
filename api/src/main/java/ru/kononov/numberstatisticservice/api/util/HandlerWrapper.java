package ru.kononov.numberstatisticservice.api.util;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 */
public class HandlerWrapper {

    private HandlerWrapper() {
    }

    public static void wrap(Logger logger,
                            String point,
                            HttpExchange exchange,
                            Consumer<HttpExchange> handler,
                            BiConsumer<HttpExchange, Exception> clientErrorHandler,
                            BiConsumer<HttpExchange, Exception> serverErrorHandler) {
        try {
            logger.info("{}.in", point);
            handler.accept(exchange);
            logger.info("{}.out", point);
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            logger.error("{}.thrown", point, e);
            clientErrorHandler.accept(exchange, e);
        } catch (Exception e) {
            logger.error("{}.thrown", point, e);
            serverErrorHandler.accept(exchange, e);
        }
    }

}
