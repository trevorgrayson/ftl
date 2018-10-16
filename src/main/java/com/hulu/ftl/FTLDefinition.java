package com.hulu.ftl;

import com.hulu.ftl.annotations.Literal;
import com.hulu.ftl.annotations.Template;
import com.hulu.ftl.formats.Parser;
import com.hulu.ftl.formats.XMLFormat;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;


public class FTLDefinition {

    ArrayList<FTLField> fields = new ArrayList<>();

    public FTLDefinition(String configFTL) {

        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(Literal.class, "!literal"));
        constructor.addTypeDescription(new TypeDescription(Literal.class, "!lit"));
        constructor.addTypeDescription(new TypeDescription(Template.class, "!template"));

        Yaml yaml = new Yaml(constructor);
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(configFTL);

        Map<String, Object> config = yaml.load(inputStream);

        config.forEach((key, val) ->
            fields.add(new FTLField(key, val))
        );
    }

    public Map parse(String filename) throws Exception {
        return parse(new File(filename));
    }

    public Map parse(File file) throws Exception {
        String filename = file.getName();

        String format = filename.substring(filename.lastIndexOf("."));
        InputStream stream = new FileInputStream(file);

        return parse(stream, format);
    }

    public Map parse(String body, String format) throws Exception {
        return parse(new ByteArrayInputStream(body.getBytes()), format);
    }

    public Map parse(InputStream stream, String format) throws Exception {

        Parser parser;

        switch(format) {
            case ".xml": parser = new XMLFormat(stream); break;
            default:
                throw new Exception("No parser for format type.");
        }

        return parser.extract(fields);
    }

}
