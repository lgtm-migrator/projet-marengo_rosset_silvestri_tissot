package ch.heigvd.dil.Utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class YAMLParser {
    public Map<String, Object> read(String path) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(path));

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        return data;
    }
}
