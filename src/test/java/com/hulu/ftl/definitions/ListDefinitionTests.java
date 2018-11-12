package com.hulu.ftl.definitions;

import com.hulu.ftl.FTLDefinition;
import com.hulu.ftl.FTLField;
import com.hulu.ftl.annotations.Literal;
import com.hulu.ftl.exceptions.FTLNotImplemented;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ListDefinitionTests {
    FTLDefinition definition;

    @Before
    public void setup() throws IOException, FTLNotImplemented {
        definition = new FTLDefinition("external_ids.ftl");
    }

    @Test
    public void findsParentList() {
        FTLField field = definition.fields.get(0);

        assertEquals(field.key, "external_ids");
        //assertEquals(field.selectors, null);
        assertEquals(field.subSelectors.size(), 2);
    }

    @Test
    public void findsListValues() {
        FTLField field0 = definition.fields.get(0).subSelectors.get(0);

        String[] tmsSelector = {};

        assertEquals("namespace", field0.key);
        assertEquals(tmsSelector, field0.selectors);

        Literal lit = (Literal) field0.annotation;
        assertEquals("namespace", field0.key);
        assertEquals("tms", lit.value);
    }
}
