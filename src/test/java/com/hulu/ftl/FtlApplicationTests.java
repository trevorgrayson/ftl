package com.hulu.ftl;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class FtlApplicationTests {
    Map program;

    @Before
    public void initialize() throws Exception {
        FTLDefinition definition = new FTLDefinition("movie.ftl");
        program =  definition.parse("movie.xml");
    }

    @Test
    public void parseProgram() {
        assertEquals("Series", program.get("type"));
//        ratings: ratings/rating
//        genres: genres/genre
//        type: progType
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

    @Test
    public void multiValue() {
        ArrayList titles = (ArrayList) program.get("title");

        assertEquals(2, titles.size());
        assertEquals("The unbelievable journey in a crazy plane", titles.get(0));
    }

    @Test
    public void multiValueReturnsOne() {
        ArrayList genres = (ArrayList) program.get("genres");
        assertEquals(1, genres.size());
        assertEquals("Sitcom", genres.get(0));
    }

    @Test
    public void actor() {
        HashMap<String, String> actor = (HashMap) program.get("actor");

        assertEquals("Richard", actor.get("first"));
        assertEquals("Anderson", actor.get("last"));
        assertEquals("Richard Anderson", actor.get("full_name"));
    }

    @Test
    public void actors() {

        List<HashMap> actors = (List) program.get("actors");

        assertEquals("Richard", actors.get(0).get("first"));
        assertEquals("Anderson", actors.get(0).get("last"));

        assertEquals("Dana", actors.get(1).get("first"));
        assertEquals("Elcar", actors.get(1).get("last"));
    }
}
