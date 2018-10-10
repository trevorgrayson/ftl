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

        for(FTLField field : fields) {
            for(String selector : field.selectors) {

                map.put(field.key,
                        field.isMultiValue ? getValues(selector) : getValue(selector)
                );

                if(map.get(selector) != null)
                    continue;

            }
        }

        return map;
    }

    public String getValue(String selector) {
        throw new NotImplementedException();
    }

    public List<String> getValues(String selector) {
        ArrayList<String> list = new ArrayList<>();
        list.add(getValue(selector));

        return list;
    }

}
