package com.hulu.ftl;

import com.hulu.ftl.exceptions.FTLNotImplemented;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class FtlFieldTests {
    Map document;

    @Before
    public void setup() throws IOException, FTLNotImplemented {
        FTLDefinition definition = new FTLDefinition("external_ids.ftl");
        document = definition.parse("program.xml");
    }

    @Test
    public void testSubList() {
        List exids = (List) document.get("external_ids");

        assertEquals(2, exids.size());
    }

    @Test
    public void relativeRootValue() {
        FTLField field = new FTLField("name", "/rootId");
        assertTrue(field.isRootRelative);
        assertEquals(field.selectors[0], "rootId");
    }

    @Test
    public void findsExternalIds() {

    }

    @Test
    public void findsHashTransformValues() {
        List externalIds = (List) document.get("external_ids");

        HashMap<String, String> externalId =
                (HashMap<String, String>) externalIds.get(0);

        assertEquals(externalId.get("namespace"), "tms");
        assertEquals(externalId.get("id"), "MV000975940000");

    }
}
