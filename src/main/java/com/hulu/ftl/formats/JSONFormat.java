package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class JSONFormat extends Parser {
    Object document;

    public JSONFormat(InputStream stream) throws IOException {
        try {
            document = Configuration.defaultConfiguration().jsonProvider().parse(stream, "utf-8");
        } catch(InvalidJsonException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public List<Object> getBySelector(String selector, Object root) {
        Object result = JsonPath.read(root, selector.replaceAll("/", ".."));
        if (result instanceof List) {
            return (List)result;
        }
        return Collections.singletonList(result);
    }

    public Object getValue(FTLField field) {
        List values = getValues(field);
        if (values.size() > 0) {
            return values.get(0);
        } else {
            return null;
        }
    }

    public List<Object> getValues(FTLField field) {
        return getValues(field, document);
    }

    public List<Object> getValues(FTLField field, Object root) {
        if (isSpecialValue(field)) {
            return Collections.singletonList(field.annotation);
        }

        if(field.hasSubFields()) {
            List list = new ArrayList<>();
            return list;

        } else {
            List values = new ArrayList<>();
            for (String selector : field.selectors) {
                values.addAll(getBySelector(selector, root));
            }
            return values;

        }
    }
}
