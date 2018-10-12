package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class XMLFormat extends Parser {

    Document document;

    XPath xPath = XPathFactory.newInstance().newXPath();

    public XMLFormat(InputStream stream) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(stream);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public ArrayList<Node> findNodes(String[] selectors) {
        String[] rootSelectors = selectors.clone();

        for(int x=0; x<rootSelectors.length; x++) {
            rootSelectors[x] = "/*/" + rootSelectors[x];
        }


        return findNodes(rootSelectors, document);
    }

    public ArrayList<Node> findNodes(String[] selectors, Node document) {
        ArrayList nodes = null;

        nodes = nodeArray("/*", document);

        for (int x = 0; x < selectors.length; x++) {
            String selector = selectors[x];

            nodes = nodeArray(selector, document);

            if (nodes.size() > 0) {
                return nodes;
            }
        }

        return nodes;
    }

    public ArrayList<String> getBySelector(String selector) {
        return getBySelector(selector, nodeArray("/*", document));
    }

    public ArrayList<String> getBySelector(String selector, ArrayList<Node> rootNodes) {
        ArrayList<String> values = new ArrayList<>();

        try {
            for(int x=0; x < rootNodes.size(); x++) {
                Node rootNode = rootNodes.get(x);

                // nodeArray(selector, rootNodes.item(x)

                NodeList nodes = (NodeList) xPath.compile(selector)
                        .evaluate(rootNode, XPathConstants.NODESET);

                for (int i = 0; i < nodes.getLength(); i++) {
                    values.add(nodes.item(i).getNodeValue());
                }
            }

        } catch (XPathExpressionException ex) {}

        return values;
    }

    private ArrayList<Node> nodeArray(String selector, Node rootNode) {
        ArrayList list = new ArrayList<>();

        try {
            NodeList nodes = (NodeList) xPath.compile(selector)
                    .evaluate(rootNode, XPathConstants.NODESET);

            for(int x=0; x < nodes.getLength(); x++) {
                list.add(nodes.item(x));
            }

        } catch (XPathExpressionException ex) {}

        return list;
    }

    @Override
    public Object getValue(FTLField field) {
        List<String> values = getValues(field);

        if(values.size() > 0) {
            return values.get(0);
        } else if (isTemplate(field.selectors)) {
            return field.selectors[0];
        }

        return null;
    }

    @Override
    public List getValues(FTLField field) {
        ArrayList rootNodes = findNodes(field.selectors);

        return getValues(field, rootNodes);
    }

    public List getValues(FTLField field, ArrayList<Node> rootNodes) {

        if(field.hasSubFields()) {
            List list = new ArrayList<>();

            for(Node rootNode : rootNodes) {
                HashMap subMap = new HashMap<>();

                for(FTLField subField : field.subSelectors) {

                    ArrayList<Node> nodes = findNodes(subField.selectors, rootNode);

                    // FIX
                    List values = getValues(subField, nodes);
                    if(values.size() > 0) {
                        subMap.put(subField.key, values.get(0));
                    } else if (isTemplate(subField.selectors)) {
                        subMap.put(subField.key, subField.selectors[0]);
                    }

                }
                list.add(subMap);
            }

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

                    if(end != 0) {
                        attrSelector += "/";
                    }

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

    private boolean isTemplate(String[] selectors) {
        boolean result = false;
        for (String selector : selectors) {
            result = result || selector.contains("$");
        }
        return result;
    }
}
