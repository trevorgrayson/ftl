package com.hulu.ftl.annotations;

import java.util.Map;

public abstract class Annotation {
    public Object value;

    public Object getValue(Map localContext) {
        return value.toString();
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
