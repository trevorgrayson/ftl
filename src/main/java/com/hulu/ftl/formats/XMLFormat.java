package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class XMLFormat extends Parser {

    Document document;

    XPath xPath = XPathFactory.newInstance().newXPath();

    public XMLFormat(String filename) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(filename);
        } catch (ParserConfigurationException|SAXException|IOException ex) {
            throw new IOException();
        }
    }

    public ArrayList<String> getBySelector(String selector) {
        ArrayList<String> values = new ArrayList<>();

        try {
            NodeList nodes = (NodeList) xPath.compile("/*/" + selector)
                    .evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                values.add(nodes.item(i).getNodeValue());
            }

        } catch (XPathExpressionException ex) {}

        return values;
    }

    @Override
    public String getValue(String selector) {
        List<String> values = getValues(selector);

        if(values.size() > 0) {
            return values.get(0);
        }

        return null;
    }

    @Override
    public List<String> getValues(String selector) {
        // TODO use | or syntax for attribute
//        if(field.subSelectors.size() > 0) {
//            HashMap subMap = new HashMap<>();
//
//            for(FTLField subField : field.subSelectors) {
//                String subSelect = "/" + subField.selectors[0];
//                subMap.put(subField.key, getValue(subSelect));
//            }
//
//            map.put(field.key, subMap);
//
//        }
        List<String> values = getBySelector(selector + "/text()");

        if(values.size() > 0) {
            return values;
        }

        // Not found, look for attr
        String[] elements = selector.split("/");

        if( elements.length > 0) {
            Integer end = elements.length > 0 ? elements.length - 1 : 0;

            String attrSelector = Arrays.stream(
                    Arrays.copyOfRange(elements, 0, end)
            ).collect(Collectors.joining("/"));

            attrSelector += "@" + elements[end];

            values = getBySelector(attrSelector);

            if(values.size() > 0) {
                return values;
            }
        }

        return new ArrayList<>();
    }
}
