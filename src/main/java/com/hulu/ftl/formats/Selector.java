package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import org.w3c.dom.Node;

import java.util.List;

public class Selector {
    private String selector;
    private String[] parts;
    private FTLField field;

    public Selector(String selector, FTLField field) {
        this.selector = selector;
        parts = selector.split("/");
        this.field = field;
    }

    public String getSelector() {
        return selector;
    }

    public String[] getParts() {
        return parts;
    }

    public String getLastPart() {
        if (parts.length > 1)
            return parts[parts.length - 1];
        return selector;
    }

    public boolean matches(Node node, List<String> ancestors) {
        int selIndex = 0, ancestorIndex = 0;
        while ((selIndex < parts.length) && (ancestorIndex < ancestors.size())) {
            if (parts[selIndex].equals(ancestors.get(ancestorIndex))) {
                ++selIndex;
                ++ancestorIndex;
            } else {
                ++ancestorIndex;
            }
        }
        return selIndex == parts.length;
    }

    public FTLField getField() {
        return field;
    }
}
