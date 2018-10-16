package com.hulu.ftl;

import com.hulu.ftl.annotations.Literal;
import com.hulu.ftl.annotations.Template;
import java.util.ArrayList;
import java.util.Map;
import java.io.*;

import com.hulu.ftl.exceptions.FTLNotImplemented;
import com.hulu.ftl.formats.Parser;
import com.hulu.ftl.formats.XMLFormat;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


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

    public Map parse(String filename)
            throws IOException, FTLNotImplemented {
        return parse(new File(filename));
    }

    public Map parse(File file)
            throws IOException, FTLNotImplemented {
        String filename = file.getName();

        String format = filename.substring(filename.lastIndexOf("."));
        InputStream stream = new FileInputStream(file);

        return parse(stream, format);
    }

    public Map parse(String body, String format) throws FTLNotImplemented, IOException {
        return parse(new ByteArrayInputStream(body.getBytes()), format);
    }

    public Map parse(InputStream stream, String format) throws FTLNotImplemented, IOException {

        Parser parser;

        switch(format) {
            case ".xml": parser = new XMLFormat(stream); break;
            default: throw new FTLNotImplemented();
        }

        return parser.extract(fields);
    }

}
