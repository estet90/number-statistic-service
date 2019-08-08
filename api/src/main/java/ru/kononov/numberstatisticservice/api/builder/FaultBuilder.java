package ru.kononov.numberstatisticservice.api.builder;

import java.util.Map;
import java.util.Objects;

public class FaultBuilder {

    public String build(String result) {
        return Map.of(
                "status", "failure",
                "result", Objects.requireNonNull(result)
        ).toString();
    }

}
