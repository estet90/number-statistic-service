package ru.kononov.numberstatisticservice.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kononov.numberstatisticservice.api.builder.FaultBuilder;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;
import static ru.kononov.numberstatisticservice.api.dto.Operation.add;
import static ru.kononov.numberstatisticservice.api.util.HandlerWrapper.wrap;
import static ru.kononov.numberstatisticservice.api.util.HttpMethodChecker.checkMethod;
import static ru.kononov.numberstatisticservice.api.util.ResponseWriter.writeResponse;

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
        wrap(logger, "AddNumberHandler.handle", add, exchange, httpExchange -> {
            checkMethod(httpExchange, "POST");
            var payload = extractPayload(httpExchange);
            logger.info("AddNumberHandler.handle payload={}", payload);
            try {
                var numberToAdd = new BigDecimal(payload);
                numberStorage.add(numberToAdd);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Невозможно преобразовать в число переданное значение " + payload, e);
            }
            writeResponse(logger, "AddNumberHandler.handle", httpExchange, HttpURLConnection.HTTP_ACCEPTED);
        }, faultBuilder::build);

    }

    private String extractPayload(HttpExchange exchange) {
        try (var inputStream = requireNonNull(exchange.getRequestBody())) {
            var result = new ByteArrayOutputStream();
            var buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Не удалось извлечь тело запроса", e);
        }
    }

}
