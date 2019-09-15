package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kononov.numberstatisticservice.api.builder.FaultBuilder;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.math.BigDecimal;
import java.net.HttpURLConnection;

import static ru.kononov.numberstatisticservice.api.util.HandlerWrapper.wrap;
import static ru.kononov.numberstatisticservice.api.util.HttpMethodChecker.checkMethod;
import static ru.kononov.numberstatisticservice.api.util.RequestReader.extractPayload;
import static ru.kononov.numberstatisticservice.api.util.ResponseWriter.*;

public class AddNumberHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger(AddNumberHandler.class);

    private final NumberStorage numberStorage;
    private final FaultBuilder faultBuilder;

    public AddNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
        this.faultBuilder = new FaultBuilder();
    }

    @Override
    public void handle(HttpExchange exchange) {
        var point = "AddNumberHandler.handle";
        wrap(logger, point, exchange,
                httpExchange -> {
                    checkMethod(httpExchange, "POST");
                    var payload = extractPayload(httpExchange);
                    try {
                        var numberToAdd = new BigDecimal(payload);
                        numberStorage.add(numberToAdd);
                    } catch (NumberFormatException e) {
                        var errorMessage = String.format("Невозможно преобразовать в число переданное значение \"%s\"", payload);
                        throw new IllegalArgumentException(errorMessage, e);
                    }
                    writeResponse(logger, point, httpExchange, HttpURLConnection.HTTP_ACCEPTED);
                },
                (httpExchange, e) -> writeClientErrorResponse(logger, point, httpExchange, e, faultBuilder::build),
                (httpExchange, e) -> writeServerErrorResponse(logger, point, httpExchange, e, faultBuilder::build)
        );

    }

}
