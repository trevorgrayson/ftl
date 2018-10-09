package com.hulu.ftl;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class FTLField {
    public String key;
    public String selector;

    public Boolean isMultiValue = false;

    public FTLField(String key, String selector) {
        this.key = key;

        if(selector.endsWith("*") && !selector.endsWith("/*")) {
            isMultiValue = true;
            selector = selector.substring(0, selector.length() - 1);
        }

        this.selector = selector;
    }

}
