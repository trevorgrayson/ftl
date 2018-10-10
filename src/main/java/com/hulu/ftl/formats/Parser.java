package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
            for(String selector : field.selectors) {

                map.put(field.key,
                    field.isMultiValue ? getValues(field) : getValue(field)
                );

                if(map.get(selector) != null)
                    continue;

            }
        }

        return map;
    }

    public Object getValue(FTLField field) {
        throw new NotImplementedException();
    }

    public List<Object> getValues(FTLField field) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(getValue(field));

        return list;
    }

}
