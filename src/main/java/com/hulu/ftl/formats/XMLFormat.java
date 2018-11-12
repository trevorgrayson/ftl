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
import java.util.*;
import java.util.regex.Pattern;
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
            rootSelectors[x] = rootSelectors[x].equals("/") ? "/" : "/*/" + rootSelectors[x];
        }

        return findNodes(rootSelectors, document);
    }

    public ArrayList<Node> findNodes(String[] selectors, Node document) {
        ArrayList nodes = new ArrayList();

        for (int x = 0; x < selectors.length; x++) {
            String selector = selectors[x];

            nodes.addAll(nodeArray(selector, document));

        }

        return nodes;
    }

    private ArrayList<String> getBySelector(String selector) {
        return getBySelector(selector, nodeArray("/*", document));
    }

    private ArrayList<String> getBySelector(String selector, List<Node> rootNodes) {
        ArrayList<String> values = new ArrayList<>();

        try {
            for(int x=0; x < rootNodes.size(); x++) {
                Node rootNode = rootNodes.get(x);

                // nodeArray(selector, rootNodes.item(x)

                NodeList nodes = (NodeList) xPath.compile(selector)
                        .evaluate(rootNode, XPathConstants.NODESET);

                for (int i = 0; i < nodes.getLength(); i++) {
                    String value = nodes.item(i).getNodeValue();
                    if (!Pattern.matches("\\s*", value))
                        values.add(value);
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

    /*
     *  Run-Time deserializing of document
     */

    @Override
    public Object getValue(FTLField field) {
        List<String> values = getValues(field);

        if(values.size() > 0) {
            return values.get(0);
        } else if (isSpecialValue(field)) {
            return field.annotation;
        }

        return null;
    }

    @Override
    public List getValues(FTLField field) {
        ArrayList rootNodes = findNodes(field.selectors);

        return getValues(field, rootNodes);
    }

    public List getValues(FTLField field, List<Node> rootNodes) {
        if (isSpecialValue(field)) {
            return Collections.singletonList(field.annotation);
        }

        if(field.hasSubFields()) {
            List list = new ArrayList<>();

            if( rootNodes.size() == 0) {
                rootNodes.add(document);
            }

            for(Node rootNode : rootNodes) {
                HashMap subMap = new HashMap<>();
                int normalValueNum = 0;

                for(FTLField subField : field.subSelectors) {
                    if(subField.isRootRelative) {
                        rootNode = document;
                    }

                    ArrayList<Node> nodes = findNodes(subField.selectors, rootNode);

                    // FIX
                    List values = getValues(subField, Collections.singletonList(rootNode));

                    if(values.size() > 0) {
                        if (!isSpecialValue(subField))
                            ++normalValueNum;

                        if (subField.isMultiValue) {
                            subMap.put(subField.key, values);
                        } else {
                            subMap.put(subField.key, values.get(0));
                        }
                    } else if (isSpecialValue(subField)) {
                        subMap.put(subField.key, field.annotation);
                    }
                }
                if (normalValueNum > 0 || list.size() == 0)
                    list.add(subMap);
            }

            return list;

        } else {

            // find element text
            List values = new ArrayList();

            // Not found, look for attr
            for(String selector : field.selectors) {
                List<Node> originNodes = rootNodes;

                if(field.isRootRelative) {
                    originNodes = new ArrayList();
                    originNodes.add(document);
                }

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

                    values.addAll(getBySelector(attrSelector));
                    if (values.size() == 0)
                        values.addAll(getBySelector(attrSelector, originNodes));
                    if (values.size() == 0)
                        values.addAll(getBySelector(selector + "/text()", originNodes));
                    if (values.size() == 0)
                        values.addAll(getBySelector("./text()", originNodes));


                    if(values.size() > 0) {
                        return values;
                    }
                }
            }
        }

        return new ArrayList();
    }
}
