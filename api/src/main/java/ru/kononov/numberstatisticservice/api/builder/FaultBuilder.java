package ru.kononov.numberstatisticservice.api.builder;

import java.util.HashMap;

public class FaultBuilder {

    public String build(String result) {
        var map = new HashMap<>();
        map.put("status", "failure");
        map.put("result", result);
        return map.toString();
    }

}
