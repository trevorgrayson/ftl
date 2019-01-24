package com.hulu.ftl.formats;

import com.hulu.ftl.FTLDefinition;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertTrue;

public class BenchmarkTests {
    private FTLDefinition definition;

    @Before
    public void initialize() throws Exception {
        definition = new FTLDefinition("program.ftl");
    }

    public void parse(String filename) throws Exception {
        FileInputStream xml = new FileInputStream(new File(filename));
        definition.parse(xml, ".xml");
    }

    @Test
    public void benchmarkMovie() throws Exception {
        parse("program.xml"); // warm cache
        long start = System.nanoTime();
        parse("program.xml");
        long end = System.nanoTime();
        double totalTime = (end - start) / 1000000.0;
        System.out.println(totalTime);
        assertTrue(totalTime < 30.0);
    }
}
