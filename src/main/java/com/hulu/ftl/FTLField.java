package com.hulu.ftl;

import com.hulu.ftl.annotations.Annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class FTLField {
    public String key;
    public String[] selectors;
    public Object annotation;

    public ArrayList<FTLField> subSelectors = new ArrayList<>();

    public Boolean isMultiValue = false;

    public FTLField(String key, Object selector) {
        switch(selector.getClass().getSimpleName()) {
            case "String": construct(key, (String) selector);
                break;
            case "LinkedHashMap":
                construct(key, (LinkedHashMap<String, Object>) selector);
                break;
            case "Literal":
            case "Template":
            case "Mapping":
                construct(key, (Annotation) selector);
                break;
        }
    }

    public void construct(String key, Annotation value) {
        this.key = key;
        selectors = new String[0];
        annotation = value;
    }

    public void construct(String key, String selector) {
        this.key = key;

        // if the selector ends with `*`, it will return multiple values.
        if(selector.endsWith("*")) {
            isMultiValue = true;

            selector = selector.substring(0, selector.length() - 1).replaceAll("[*]$", "");
        }

        // `|` represents fallback selectors
        selectors = selector.split("[|]");

        for(int x=0; x<selectors.length; x++) {
            selectors[x] = selectors[x].replaceAll("[*]$", "");
        }
    }

    public void construct(String key, LinkedHashMap<String, Object> selector) {
        this.key = key;

        selectors = Arrays.copyOf(selector.keySet().toArray(), selector.values().size(), String[].class);

        for(int x=0; x<selectors.length; x++) {
            String sel = selectors[x];

            if(sel.endsWith("*")) {
                isMultiValue = true;
            }

            selectors[x] = selectors[x].replaceAll("[*]$", "");
            // lazy. doubling memory, fix this.
            selector.put(selectors[x], selector.get(sel));
        }

        for(String sel : selectors) {
            LinkedHashMap<String, Object> subValues = (LinkedHashMap) selector.get(sel);

            subValues.forEach((k, val) -> {
                // meh? .replaceAll("[*]$", "")
                if (val instanceof String)
                    subSelectors.add(new FTLField(k, ((String)val)));
                else
                    subSelectors.add(new FTLField(k, val));
            });
        }
    }

    public Boolean hasSubFields() {
        return subSelectors.size() > 0;
    }

}
