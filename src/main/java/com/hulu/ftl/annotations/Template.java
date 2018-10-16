package com.hulu.ftl.annotations;

import java.util.Map;

public class Template extends Annotation {
    private String value;

    public Template(String value) {
        this.value = value;
    }

    @Override
    public String getValue(Map map) {
        String input = value;
        for (Object item : map.entrySet()) {
            Map.Entry entry = (Map.Entry)item;
            Object value = entry.getValue();
            if (value != null) {
                input = input.replace("$" + entry.getKey(), value.toString());
            }
        }
        return input;
    }

    @Override
    public String toString() {
        return value;
    }
}
