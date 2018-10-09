package com.hulu.ftl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProgramTests {
    Map program;

    @Before
    public void initialize() throws Exception {
        FTLDefinition definition = new FTLDefinition("program.ftl");
        program =  definition.parse("program.xml");
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
    public void genres() {
        ArrayList titles = (ArrayList) program.get("genres");

        assertEquals(2, titles.size());
        assertEquals("The unbelievable journey in a crazy plane", titles.get(0));
    }

    @Test
    public void multiValueReturnsOne() {
        ArrayList genres = (ArrayList) program.get("genres");
        assertEquals(1, genres.size());
        assertEquals("Sitcom", genres.get(0));
    }

}
