package ch.heigvd.dil.util;


import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Mets à disposition un parser Yaml, retournant une map à partir d'un fichier ou d'une liste de String
 *
 * @author Géraud Silvestri
 */
public class YAMLParser {
    /**
     * Parse un fichier YAML et renvoi une map contenant les paires clé-valeur
     *
     * @param path chemin du fichier à parse
     * @return toutes les données du fichier donné
     * @throws IOException retourne une exception si le fichier n'est pas trouvé ou valide
     */
    public Map<String, Object> parseFromFile(String path) throws IOException {
        Map<String, Object> data;
        try (InputStream inputStream = new FileInputStream(path)) {
            Yaml yaml = new Yaml();
            data = yaml.load(inputStream);
        }

        return data;
    }

    /**
     * Crée une map à partir d'une liste de String
     *
     * @param content liste de String correspondant chacune à une ligne
     * @return la map contenant les données, vide si la liste n'est pas valide
     */
    public static Map<String, Object> parseFromString(List<String> content) {
        Map<String, Object> data = new HashMap<>();
        List<String[]> temp = new ArrayList<>();

        for (String line : content) temp.add(line.split(":"));

        for (String[] line : temp) {
            if (line.length == 2) data.put(line[0], line[1]);
        }

        return data;
    }

    /**
     * Crée une map à partir d'un String
     *
     * @param content string contenant les données YAML
     * @return la map contenant les données, vide si la liste est vide
     */
    public static Map<String, Object> parseFromString(String content) {
        return parseFromString(new ArrayList<>(Arrays.asList(content.split("\r\n"))));
    }
}
