package com.hulu.ftl.annotations;

import java.util.Map;

public class Mapping extends Annotation {
    private Map<String, Object> map;
    public Mapping(String value, Map map) {
        this.value = value;
        this.map = map;
    }

    @Override
    public Object getValue(Map localContext) {
        String item = (String) localContext.get(value);
        for (String key: map.keySet()) {
            if (item.matches(key)) {
                return map.get(key);
            }
        }
        return null;
    }
}
