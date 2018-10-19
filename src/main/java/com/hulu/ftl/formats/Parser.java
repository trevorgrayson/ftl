package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import com.hulu.ftl.annotations.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Parser {
    /*
        Iterates over FTLFields and gets values back in a Map
     */

    public Map extract(ArrayList<FTLField> fields) {
        Map map = new HashMap<>();

        for(FTLField field : fields) {
            addToMap(map, field);
        }

        for(FTLField field : fields) {
            Object result = postprocess(map.get(field.key), map);
            map.put(field.key, result);
        }


        return map;
    }

    public Object getValue(FTLField field) {
        throw new UnsupportedOperationException("getValue method not implemented for this format!");
    }

    public List<Object> getValues(FTLField field) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(getValue(field));

        return list;
    }

    private Object postprocess(Object value, Map localContext) {
        if (value instanceof Annotation) {
            return ((Annotation)value).getValue(localContext);
        } else if (value instanceof List) {
            List list = (List)value;
            for (int index = 0; index < list.size(); ++index) {
                list.set(index, postprocess(list.get(index), localContext));
            }
            return list;
        } else if (value instanceof Map) {
            Map valueMap = (Map)value;
            for (Object item : valueMap.entrySet()) {
                Map.Entry entry = (Map.Entry)item;
                valueMap.put(entry.getKey(), postprocess(entry.getValue(), valueMap));
            }
            return valueMap;
        }
        return value;
    }

    private void addToMap(Map map, FTLField field) {
        if (field.isMultiValue) {
            List values = (List)map.get(field.key);
            if (values != null) {
                values.addAll(getValues(field));
            } else {
                values = getValues(field);
            }
            map.put(field.key, values);
        } else {
            map.put(field.key, getValue(field));
        }

    }

    boolean isSpecialValue(FTLField field) {
        return field.annotation != null;
    }
}
