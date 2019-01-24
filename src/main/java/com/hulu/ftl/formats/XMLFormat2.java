package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class XMLFormat2 extends Parser {
    Map<String, Object> result;
    private String topLevelElement;
    private HashMap<Object, Integer> listOrder;

    public XMLFormat2(InputStream stream, ArrayList<FTLField> fields) throws IOException  {
        Document document;
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(stream);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new IOException(ex.getMessage());
        }

        MultiValuedMap<String, Selector> selectorMap = new ArrayListValuedHashMap<>();
        listOrder = new HashMap<>();
        result = new HashMap<>();
        topLevelElement = document.getFirstChild().getNodeName();
        contructSelectorMap(fields, selectorMap, result, true);
        LinkedList<String> ancestors = new LinkedList<>();
        ancestors.addLast(getName(document));
        recurse(document, ancestors, selectorMap, result);
    }

    public void contructSelectorMap(List<FTLField> allFields, MultiValuedMap<String, Selector> selectorMap,
                                    Map<String, Object> result, boolean insertNull) {
        for (FTLField field : allFields) {
            for (String selector : field.selectors) {
                Selector selectorObj = new Selector(selector, field);
                selectorMap.put(selectorObj.getLastPart(), selectorObj);
            }
            if (insertNull)
                result.put(field.key, null);
            if (field.isMultiValue)
                result.put(field.key, new ArrayList<>());
            else if (field.annotation != null)
                result.put(field.key, field.annotation);
        }

    }

    public Object getValue(FTLField field) {
        return "";
    }

    private void handleNode(Node child, LinkedList<String> ancestors,
                            MultiValuedMap<String, Selector> selectorMap, Map<String, Object> result) {
        ancestors.addLast(getName(child));
        process(child, ancestors, selectorMap, result);
        recurse(child, ancestors, selectorMap, result);
        ancestors.removeLast();
    }

    public void recurse(Node root, LinkedList<String> ancestors,
                        MultiValuedMap<String, Selector> selectorMap, Map<String, Object> result) {
        // iterate through attribute children
        NamedNodeMap attributes = root.getAttributes();
        if (attributes != null) {
            for (int index = 0; index < attributes.getLength(); ++index) {
                Node attribute = attributes.item(index);
                handleNode(attribute, ancestors, selectorMap, result);
            }
        }

        // iterate through element children
        Node child = root.getFirstChild();
        if (child == null)
            return;

        do {
            handleNode(child, ancestors, selectorMap, result);
        } while ((child = child.getNextSibling()) != null);
    }

    private String getName(Node node) {
        String name = node.getNodeName();
        if (name.equals(topLevelElement))
            name = "/";
        return name;
    }

    public void process(Node node, LinkedList<String> ancestors,
                        MultiValuedMap<String, Selector> selectorMap, Map<String, Object> result) {
        if (node.getNodeType() == node.TEXT_NODE) {
            return;
        }

        for (Selector data : selectorMap.get(getName(node))) {
             Object text = (node.getNodeType() == node.ELEMENT_NODE) ? node.getTextContent() : node.getNodeValue();

            if (!data.matches(node, ancestors))
                continue;

            FTLField field = data.getField();

            List<FTLField> subfields = field.selectorToSubfields.get(data.getSelector());
            if (subfields.size() > 0) {
                Map<String, Object> subfieldResult = new HashMap<>();
                MultiValuedMap<String, Selector> subfieldSelectorMap = new ArrayListValuedHashMap<>();
                contructSelectorMap(subfields, subfieldSelectorMap, subfieldResult, false);
                recurse(node, ancestors, subfieldSelectorMap, subfieldResult);
                if (subfieldSelectorMap.containsKey(".")) { // handle "." to mean "parent node"
                    for (Selector thisSelector : subfieldSelectorMap.get("."))
                        subfieldResult.put(thisSelector.getField().key, text);
                }
                text = subfieldResult;
            }

            if (field.isMultiValue) {
                ArrayList<Object> list = (ArrayList<Object>) result.get(field.key);
                list.add(text);
                listOrder.put(text, field.selectorIndex.get(data.getSelector()));
                // this is to ensure subselectors produce data in the correct order:
                list.sort(Comparator.comparing(listOrder::get));
                // I'm sure there's a better way to do this
            } else {
                result.putIfAbsent(field.key, text);
            }

        }
    }

    public Map extract(ArrayList<FTLField> fields) {
        return postprocess(result, fields);
    }

}
