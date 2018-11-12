package com.hulu.ftl;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ProgramTests {
    Map program;

    @Before
    public void initialize() throws Exception {
        FTLDefinition definition = new FTLDefinition("program.ftl");
        program =  definition.parse(new File("movie.xml"));
    }

    @Test
    public void selectElementRemap() {
        assertEquals("1982-12-31", program.get("premiere"));
    }

    @Test
    public void fallbackValue() {
        assertEquals("1008385", program.get("fallback"));
    }

    @Test
    public void selectByAttribute() {
        assertEquals("EP000000060001", program.get("id"));
    }

    @Test
    public void singleValue() {
        String description = "I am the very model of a modern major general.";
        assertEquals(description, program.get("description"));
    }

//    @Test
//    public void multiRatings() {
//        ArrayList ratings = (ArrayList) program.get("ratings");
//
//        assertEquals(1, ratings.size());
//        assertEquals("G", ratings.get(0));
//    }

    @Test
    public void multiValueReturnsOne() {
        ArrayList genres = (ArrayList) program.get("genres");
        assertEquals(1, genres.size());
        assertEquals("Sitcom", ((Map)genres.get(0)).get("name"));
    }

    @Test
    public void mapping() {
        String type = (String) program.get("entity_type");
        assertEquals("episode", type);
    }

    @Test
    public void findsHashTransforms() {
        List externalIds = (List) program.get("external_ids");

        assertEquals(externalIds.size(), 2);
    }

    @Test
    public void findsHashTransformValues() {
        List externalIds = (List) program.get("external_ids");

        HashMap<String, String> externalId =
                (HashMap<String, String>) externalIds.get(0);

        assertEquals(externalId.get("namespace"), "tms");
        assertEquals(externalId.get("id"), "EP000000060001");
    }

    @Test
    public void rootRelativeValue() {

    }

    @Test
    public void rootRelativeSubValue() {

    }

    // external ids
}
