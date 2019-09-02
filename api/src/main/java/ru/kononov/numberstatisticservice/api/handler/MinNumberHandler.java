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
import static ru.kononov.numberstatisticservice.api.util.HandlerWrapper.wrap;
import static ru.kononov.numberstatisticservice.api.util.HttpMethodChecker.checkMethod;
import static ru.kononov.numberstatisticservice.api.util.ResponseWriter.*;

public class MinNumberHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger(MinNumberHandler.class);

    private final NumberStorage numberStorage;
    private final FaultBuilder faultBuilder;

    public MinNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
        this.faultBuilder = new FaultBuilder();
    }

    @Override
    public void handle(HttpExchange exchange) {
        var point = "MinNumberHandler.handle";
        wrap(logger, point, exchange,
                httpExchange -> {
                    checkMethod(httpExchange, "GET");
                    var result = ofNullable(numberStorage.min()).map(BigDecimal::toString).orElse(null);
                    writeOkResponse(logger, point, httpExchange, () -> result);
                },
                (httpExchange, e) -> writeClientErrorResponse(logger, point, httpExchange, e, ex -> faultBuilder.build(ex.getMessage())),
                (httpExchange, e) -> writeServerErrorResponse(logger, point, httpExchange, e, ex -> faultBuilder.build(ex.getMessage()))
        );
    }
}
