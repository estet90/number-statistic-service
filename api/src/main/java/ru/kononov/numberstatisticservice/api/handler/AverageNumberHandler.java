package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kononov.numberstatisticservice.api.builder.FaultBuilder;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.math.BigDecimal;
import java.net.HttpURLConnection;

import static java.util.Optional.ofNullable;
import static ru.kononov.numberstatisticservice.api.dto.Operation.averageNumber;
import static ru.kononov.numberstatisticservice.api.util.HandlerWrapper.wrap;
import static ru.kononov.numberstatisticservice.api.util.ResponseWriter.writeResponse;

public class AverageNumberHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger();

    private final NumberStorage numberStorage;
    private final FaultBuilder faultBuilder;

    public AverageNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
        this.faultBuilder = new FaultBuilder();
    }

    @Override
    public void handle(HttpExchange exchange) {
        wrap(logger, "AverageNumberHandler.handle", averageNumber, exchange, httpExchange -> {
            var method = httpExchange.getRequestMethod();
            if ("GET".equals(method)) {
                var result = ofNullable(numberStorage.average()).map(BigDecimal::toString).orElse(null);
                return writeResponse(
                        logger, "AverageNumberHandler.handle", httpExchange,
                        () -> result,
                        HttpURLConnection.HTTP_OK
                );
            } else {
                throw new UnsupportedOperationException(String.format("Метод %s не поддерживается", method));
            }
        }, faultBuilder::build);
    }
}
