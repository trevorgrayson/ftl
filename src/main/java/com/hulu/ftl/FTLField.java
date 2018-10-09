package com.hulu.ftl;


public class FTLField {
    public String key;
    public String selector;
    public String[] selectors;

    public Boolean isMultiValue = false;

    public FTLField(String key, String selector) {
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

}
