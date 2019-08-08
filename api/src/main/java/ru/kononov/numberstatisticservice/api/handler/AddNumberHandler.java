package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ru.kononov.numberstatisticservice.api.builder.FaultBuilder;
import ru.kononov.numberstatisticservice.api.builder.ResponseBuilder;
import ru.kononov.numberstatisticservice.api.util.HandlerWrapper;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class AddNumberHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger(AddNumberHandler.class);

    private final NumberStorage numberStorage;
    private final ResponseBuilder responseBuilder;
    private final FaultBuilder faultBuilder;

    public AddNumberHandler(NumberStorage numberStorage) {
        this.numberStorage = numberStorage;
        this.responseBuilder = new ResponseBuilder();
        this.faultBuilder = new FaultBuilder();
    }

    @Override
    public void handle(HttpExchange exchange) {
        ThreadContext.put("operationName", "addNumber");
        HandlerWrapper.wrap(logger, "AddNumberHandler.handle", exchange, httpExchange -> {
            var method = httpExchange.getRequestMethod();
            if ("POST".equals(method)) {
                var payload = extractPayload(httpExchange);
                logger.info("AddNumberHandler.handle payload={}", payload);
                try {
                    var numberToAdd = new BigDecimal(payload);
                    numberStorage.add(numberToAdd);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Невозможно преобразовать в число переданное значение " + payload, e);
                }
                return HandlerWrapper.writeResponse(
                        logger, "AddNumberHandler.handle", httpExchange,
                        () -> responseBuilder.build("Добавлено число " + payload),
                        200
                );
            } else {
                throw new UnsupportedOperationException(String.format("Метод %s не поддерживается", method));
            }
        }, faultBuilder::build);

    }

    private String extractPayload(HttpExchange exchange) {
        try (var inputStream = exchange.getRequestBody()) {
            var result = new ByteArrayOutputStream();
            var buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
