package com.hulu.ftl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class FTLField {
    public String key;
    public String selector;
    public String[] selectors;

    public ArrayList<FTLField> subSelectors = new ArrayList<>();

    public Boolean isMultiValue = false;

    public FTLField(String key, Object selector) {
        switch(selector.getClass().getSimpleName()) {
            case "String": construct(key, (String) selector);
                break;
            case "LinkedHashMap":
                construct(key, (LinkedHashMap<String, Object>) selector);
                break;
        }
    }

    public void construct(String key, String selector) {
        this.key = key;

        // if the selector ends with `*`, it will return multiple values.
        if(selector.endsWith("*") && !selector.endsWith("/*")) {
            isMultiValue = true;
            selector = selector.substring(0, selector.length() - 1);
        }

        // `|` represents fallback selectors
        selectors = selector.split("[|]");

        this.selector = selector;
    }

    public void construct(String key, LinkedHashMap<String, Object> selector) {
        this.key = key;
        selectors = Arrays.copyOf(selector.keySet().toArray(), selector.values().size(), String[].class);

        LinkedHashMap<String, String> subValues = (LinkedHashMap) selector.get(selectors[0]);

        subValues.forEach((k, val) ->
                subSelectors.add(new FTLField(k, val))
        );

    }

}
