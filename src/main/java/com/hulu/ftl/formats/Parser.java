package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Parser {

    public Map extract(ArrayList<FTLField> fields) {
        Map map = new HashMap<>();

        fields.forEach(field -> map.put(field.key,
            field.isMultiValue ? getValues(field) : getValue(field)
        ));

        return map;
    }

    public String getValue(FTLField field) {
        throw new NotImplementedException();
    }

    public List<String> getValues(FTLField field) {
        ArrayList<String> list = new ArrayList<>();
        list.add(getValue(field));

        return list;
    }

}
