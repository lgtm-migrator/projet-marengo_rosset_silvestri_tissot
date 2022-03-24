package ch.heigvd.dil.Util;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class YAMLParser {
    public Map<String, Object> read(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        inputStream.close();
        return data;
    }
}
