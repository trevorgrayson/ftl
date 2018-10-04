package com.hulu.ftl;

import com.sun.deploy.util.ArrayUtil;
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
public class FtlApplicationTests {
    Map program;

    @Before
    public void initialize() throws Exception {
        FTLDefinition definition = new FTLDefinition("program.ftl");
        program =  definition.parse("program.xml");
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
        assertEquals("1982-12-30", program.get("premiere"));
    }

    @Test
    public void selectByAttribute() {
        assertEquals("EP000000060001", program.get("id"));
    }

    @Test
    public void singleValue() {
        String description = "Rene treads a delicate path with local German officers in order to obtain the small luxuries of life.";
        assertEquals(description, program.get("description"));
    }

    @Test
    public void multiValue() {
        ArrayList titles = (ArrayList) program.get("title");

        assertEquals(2, titles.size());
        assertEquals("'Allo 'Allo!", titles.get(0));
    }

    @Test
    public void multiValueReturnsOne() {
        ArrayList genres = (ArrayList) program.get("genres");
        assertEquals(1, genres.size());
        assertEquals("Sitcom", genres.get(0));
    }

}
