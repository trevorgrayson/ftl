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

        return postprocess(map, fields);
    }

    public Map postprocess(Map map, ArrayList<FTLField> fields) {
        for(FTLField field : fields) {
            Object result = postprocess(map.get(field.key), map, false);
            map.put(field.key, result);

        }

        for(FTLField field : fields) {
            Object result = postprocess(map.get(field.key), map, true);
            map.put(field.key, result);
            if (field.key.startsWith("$"))
                map.remove(field.key);
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

    protected Object postprocess(Object value, Map localContext, boolean removeTemp) {
        if (value instanceof Annotation) {
            return ((Annotation)value).getValue(localContext);
        } else if (value instanceof List) {
            List list = (List)value;
            for (int index = 0; index < list.size(); ++index) {
                list.set(index, postprocess(list.get(index), localContext, removeTemp));
            }
            return list;
        } else if (value instanceof Map) {
            Map valueMap = (Map)value;
            for (Object item : valueMap.entrySet()) {
                Map.Entry entry = (Map.Entry)item;
                valueMap.put(entry.getKey(), postprocess(entry.getValue(), valueMap, removeTemp));
                if (removeTemp && entry.getKey().toString().startsWith("$")) {
                    valueMap.remove(entry.getKey());
                }
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
