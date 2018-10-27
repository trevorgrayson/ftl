package com.hulu.ftl;

import com.hulu.ftl.annotations.Annotation;
import com.hulu.ftl.annotations.Literal;

import java.util.*;

public class FTLField {
    public String key;
    public String[] selectors;
    public Object annotation;

    public ArrayList<FTLField> subSelectors = new ArrayList<>();

    public Boolean isMultiValue = false;
    public Boolean isRootRelative = false;

    public FTLField(String key, Annotation selector) {
        construct(key, (Annotation) selector);
    }

    public FTLField(String key, Object selector) {
        switch(selector.getClass().getSimpleName()) {
            case "String": construct(key, (String) selector);
                break;
            case "LinkedHashMap":
                // Needs to be cleaned up
                LinkedHashMap<String, Object> sel = (LinkedHashMap<String, Object>) selector;
                Object firstValue = sel.entrySet().iterator().next().getValue();


                if (firstValue != null) {
                    switch(firstValue.getClass().getSimpleName()) {
                        case "Literal":
                        case "String":
                            construct(key);
                            isRootRelative = true;
                            isMultiValue = true;
                            for(Map.Entry<String, Object> entry : sel.entrySet()) {
                                subSelectors.add(
                                    new FTLField(entry.getKey(), entry.getValue())
                                );
                            }

                            break;
                        default: construct(key, sel);
                    }
                }

                break;
            case "Literal":
            case "Template":
            case "Mapping":
                construct(key, (Annotation) selector);
                break;
            default:
                System.out.println("UT O, we didn't do:" + selector.getClass().getSimpleName());
        }
    }

    public void construct(String key, Annotation value) {
        this.key = key;
        selectors = new String[0];
        annotation = value;
    }

    public void construct(String key) {
        this.key = key;
        selectors = new String[0]; // shouldn't have to do this everywhere.
        isRootRelative = true;
    }

    public void construct(String key, String selector) {
        this.key = key;
        // if selector starts with `/`, it will be relative to the root document node.
        if(selector.startsWith("/")) {
            isRootRelative = true;
            selector = selector.substring(1);
        }

        // if the selector ends with `*`, it will return multiple values.
        if(selector.endsWith("*")) {
            isMultiValue = true;

            selector = selector.substring(0, selector.length() - 1).replaceAll("[*]$", "");
        }

        // `|` represents fallback selectors
        selectors = selector.split("[|]");

        for(int x=0; x<selectors.length; x++) {
            selectors[x] = selectors[x].replaceAll("[*]$", "");
        }
    }

    public void construct(String key, LinkedHashMap<String, Object> selector) {
        this.key = key;

        // bad
        selectors = Arrays.copyOf(selector.keySet().toArray(), selector.values().size(), String[].class);

        for(int x=0; x<selectors.length; x++) {
            String sel = selectors[x];

            if(sel.endsWith("*")) {
                isMultiValue = true;
            }

            selectors[x] = selectors[x].replaceAll("[*]$", "");
            // lazy. doubling memory, fix this.
            selector.put(selectors[x], selector.get(sel));
        }

        for(String sel : selectors) {
            if(selector.get(sel) instanceof LinkedHashMap) {  // this turnary is horrible, extract to map
                LinkedHashMap<String, Object> subValues = (LinkedHashMap) selector.get(sel);

                subValues.forEach((k, val) -> {
                    // meh? .replaceAll("[*]$", "")
                    subSelectors.add(new FTLField(k, val));
                });

            } else if(selector.get(sel) instanceof String) {
                //Literal
                //String
                //WTF?
                subSelectors.add(new FTLField("yuck", selector.get(sel)));
            }
//            else if(selector.get(sel) instanceof List) { // HERE, not LinkedHashMap, then what?
//                List<Object> subValues = (List) selector.get(sel);
//
//                subValues.forEach(hash -> {
//                    subSelectors.add(new FTLField(hash))
//                });
//            }
        }
    }

    public Boolean hasSubFields() {
        return subSelectors.size() > 0;
    }

    public String toString() {
        String repr = "<FTLField " + key + ": " + String.join("|", selectors) + ">";

        for(FTLField sub : subSelectors) {
            repr += "\n    " + subSelectors.toString();
        }

        return repr;
    }

}
