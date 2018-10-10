package com.hulu.ftl;

import com.hulu.ftl.formats.Parser;
import com.hulu.ftl.formats.XMLFormat;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FTLDefinition {

    ArrayList<FTLField> fields = new ArrayList<>();

    public FTLDefinition(String configFTL) {

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(configFTL);

        Map<String, Object> config = yaml.load(inputStream);

        config.forEach((key, val) ->
            fields.add(new FTLField(key, val))
        );
    }

    public Map parse(String filename) throws Exception {
        String format = filename.substring(filename.lastIndexOf("."));
        HashMap values = new HashMap();

        fields.forEach((field) ->
            values.put(field.key, field.selectors)
        );

        Parser parser;

        switch(format) {
            case ".xml": parser = new XMLFormat(filename); break;
            default:
                throw new Exception("No parser for format type.");
        }

        return parser.extract(fields);
    }

}
