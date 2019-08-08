package ru.kononov.numberstatisticservice.api.builder;

import java.util.HashMap;

public class ResponseBuilder {

    public String build(String result) {
        var map = new HashMap<>();
        map.put("status", "success");
        map.put("result", result);
        return map.toString();
    }

}
