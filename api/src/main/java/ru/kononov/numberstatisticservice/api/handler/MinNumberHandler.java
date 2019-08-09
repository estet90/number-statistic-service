package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kononov.numberstatisticservice.api.builder.FaultBuilder;
import ru.kononov.numberstatisticservice.api.builder.ResponseBuilder;
import ru.kononov.numberstatisticservice.api.util.HandlerWrapper;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;
import static ru.kononov.numberstatisticservice.api.dto.Operation.minNumber;

public class MinNumberHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger();

    private final NumberStorage numberStorage;
    private final ResponseBuilder responseBuilder;
    private final FaultBuilder faultBuilder;

    public MinNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
        this.responseBuilder = new ResponseBuilder();
        this.faultBuilder = new FaultBuilder();
    }

    @Override
    public void handle(HttpExchange exchange) {
        HandlerWrapper.wrap(logger, "MinNumberHandler.handle", minNumber, exchange, httpExchange -> {
            var method = httpExchange.getRequestMethod();
            if ("GET".equals(method)) {
                var result = ofNullable(numberStorage.min()).map(BigDecimal::toString).orElse(null);
                return HandlerWrapper.writeResponse(
                        logger, "MinNumberHandler.handle", httpExchange,
                        () -> responseBuilder.build(result),
                        200
                );
            } else {
                throw new UnsupportedOperationException(String.format("Метод %s не поддерживается", method));
            }
        }, faultBuilder::build);
    }
}
