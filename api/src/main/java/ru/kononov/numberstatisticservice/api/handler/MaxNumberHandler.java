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
import static ru.kononov.numberstatisticservice.api.dto.Operation.max;
import static ru.kononov.numberstatisticservice.api.util.HandlerWrapper.wrap;
import static ru.kononov.numberstatisticservice.api.util.HttpMethodChecker.checkMethod;
import static ru.kononov.numberstatisticservice.api.util.ResponseWriter.writeResponse;

public class MaxNumberHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger(MaxNumberHandler.class);

    private final NumberStorage numberStorage;
    private final FaultBuilder faultBuilder;

    public MaxNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
        this.faultBuilder = new FaultBuilder();
    }

    @Override
    public void handle(HttpExchange exchange) {
        wrap(logger, "MaxNumberHandler.handle", max, exchange, httpExchange -> {
            checkMethod(httpExchange, "GET");
            var result = ofNullable(numberStorage.max()).map(BigDecimal::toString).orElse(null);
            return writeResponse(
                    logger, "MaxNumberHandler.handle", httpExchange,
                    () -> result,
                    HttpURLConnection.HTTP_OK
            );
        }, faultBuilder::build);
    }
}
