package com.hulu.ftl;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JSONTests {
    Map program;

    @Before
    public void initialize() throws Exception {
        FTLDefinition definition = new FTLDefinition("jsontest.ftl");
        program =  definition.parse(new File("program.json"));
        System.out.println(program);
    }

    @Test
    public void singleValue() {
        assertEquals("Pilot", program.get("title"));
    }
}
