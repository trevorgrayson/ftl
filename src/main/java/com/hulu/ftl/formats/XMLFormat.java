package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeListBase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new IOException();
        }
    }

    public NodeList findNodes(String[] selectors) {
        return findNodes(selectors, document);
    }

    public NodeList findNodes(String[] selectors, Node document) {
        NodeList nodes = null;

        try {
            nodes = (NodeList) xPath.compile("/*")
                    .evaluate(document, XPathConstants.NODESET);

            for (int x = 0; x < selectors.length; x++) {
                String selector = selectors[x];

                nodes = (NodeList) xPath.compile("/*/" + selector)
                        .evaluate(document, XPathConstants.NODESET);

                if (nodes.getLength() > 0) {
                    return nodes;
                }
            }

        } catch (XPathExpressionException ex) { }

        return nodes;
    }

    public ArrayList<String> getBySelector(String selector) {
        try {
            NodeList rootNodes = (NodeList) xPath.compile("/*")
                    .evaluate(document, XPathConstants.NODESET);

            return getBySelector(selector, rootNodes);

        } catch (XPathExpressionException ex) {}

        return new ArrayList<>();
    }

    public ArrayList<String> getBySelector(String selector, NodeList rootNodes) {
        ArrayList<String> values = new ArrayList<>();

        try {
            for(int x=0; x < rootNodes.getLength(); x++) {
                Node rootNode = rootNodes.item(x);

                NodeList nodes = (NodeList) xPath.compile(selector)
                        .evaluate(rootNode, XPathConstants.NODESET);

                for (int i = 0; i < nodes.getLength(); i++) {
                    values.add(nodes.item(i).getNodeValue());
                }
            }

        } catch (XPathExpressionException ex) {}

        return values;
    }

    @Override
    public Object getValue(FTLField field) {
        List<String> values = getValues(field);

        if(values.size() > 0) {
            return values.get(0);
        }

        return null;
    }

    @Override
    public List getValues(FTLField field) {
        NodeList rootNodes = findNodes(field.selectors);

        return getValues(field, rootNodes);
    }

    public List getValues(FTLField field, NodeList rootNodes) {

        if(field.hasSubFields()) {
            List list = new ArrayList<>();
            HashMap subMap = new HashMap<>();

            for(FTLField subField : field.subSelectors) {
                // clean this up. ignoring getSelctor
                try {
                    NodeList nodes = (NodeList) xPath.compile(subField.selectors[0])
                            .evaluate(rootNodes.item(0), XPathConstants.NODESET);

                    subMap.put(subField.key, getValues(subField, nodes));
                } catch( XPathExpressionException ex) {}
            }

            list.add(subMap);

            return list;

        } else {

            // find element text
            List values = getBySelector("./text()", rootNodes);

            if(values.size() > 0) {
                return values;
            }

            // Not found, look for attr
            for(String selector : field.selectors) {
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
            }

        }

        return new ArrayList();
    }
}
