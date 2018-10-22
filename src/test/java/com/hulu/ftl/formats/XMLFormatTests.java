package com.hulu.ftl.formats;

import com.hulu.ftl.FTLField;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class XMLFormatTests {
    XMLFormat definition;

    @Before
    public void initialize() throws Exception {
        definition = new XMLFormat(new FileInputStream("program.xml"));
    }

    @Test
    public void findNodes() {
        FTLField field = new FTLField("fieldName", "cast/member/name");
        ArrayList<Node> nodes = definition.findNodes(field.selectors);

        for(Node node : nodes) {
            assertEquals("name", node.getNodeName());
        }

        assertEquals(10, nodes.size());
    }

    @Test
    public void findMissingNodes() {
        FTLField field = new FTLField("fieldName", "notPresent/member/name");
        ArrayList nodes = definition.findNodes(field.selectors);

        assertEquals(ArrayList.class, nodes.getClass());
        assertEquals(0, nodes.size());
    }

    @Test
    public void selectAttribute() {
        FTLField field = new FTLField("fieldName", "TMSId");
        String value = (String) definition.getValue(field);

        assertEquals("MV000975940000", value);
    }

    @Test
    public void selectElement() {
        FTLField field = new FTLField("fieldName", "colorCode");
        assertEquals("Color", definition.getValue(field));

    }

    @Test
    public void findSubFields() {

    }

}
