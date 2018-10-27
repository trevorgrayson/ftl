package com.hulu.ftl.definitions;

import com.hulu.ftl.FTLDefinition;
import com.hulu.ftl.FTLField;
import com.hulu.ftl.annotations.Literal;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class HashDefinitionTests {
    FTLDefinition definition;

    @Before
    public void setup() {
        definition = new FTLDefinition("external_ids_legacy.ftl");
    }

    @Test
    public void findsParentList() {
        FTLField field = definition.fields.get(0);

        assertEquals(field.key, "external_ids");
        assertEquals(field.subSelectors.size(), 2);
    }

    @Test
    public void findsListValues() {
        FTLField field0 = definition.fields.get(0).subSelectors.get(0);
        FTLField field1 = definition.fields.get(0).subSelectors.get(1);

        FTLField field2 = definition.fields.get(1).subSelectors.get(0);

        String[] tmsSelector = {};

        assertEquals(field0.key, "namespace");

        assertEquals(field1.key, "id");

        Literal lit = (Literal) field2.annotation;
        assertEquals("namespace", field2.key);
        assertEquals("gracenote_episode", lit.value);
        assertEquals(tmsSelector, field2.selectors);
//        assertEquals(field.subSelectors.size(), 2);
    }
}
