package ru.kononov.numberstatisticservice.api.builder;

import java.util.Map;
import java.util.Objects;

public class ResponseBuilder {

    public String build(String result) {
        return Map.of(
                "status", "success",
                "result", Objects.requireNonNull(result)
        ).toString();
    }

}
