package ru.kononov.numberstatisticservice.api.builder;

import javax.inject.Inject;
import java.util.HashMap;

public class FaultBuilder {

    @Inject
    public FaultBuilder() {
    }

    public String build(Exception e) {
        var map = new HashMap<>();
        map.put("status", "failure");
        map.put("result", e.getMessage());
        return map.toString();
    }

}
