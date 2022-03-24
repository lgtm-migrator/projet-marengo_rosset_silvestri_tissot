package ch.heigvd.dil.Util;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Géraud Silvestri
 * Mets à disposition un parser Yaml, retournant une map à partir d'un fichier ou d'une liste de String
 */
public class YAMLParser {
    /**
     * Parse un fichier YAML et renvoi une map contenant les paires clé-valeur
     *
     * @param path chemin du fichier à parse
     * @return toutes les données du fichier donné
     * @throws IOException retourne une exception si le fichier n'est pas trouver ou valide
     */
    public Map<String, Object> parseFromFile(String path) throws Exception {
        Map<String, Object> data = null;
        try (InputStream inputStream = new FileInputStream(path)) {
            Yaml yaml = new Yaml();
            data = yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found");
        } catch (Exception e) {
            throw new IOException("File isn't a YAML file");
        }

        return data;
    }

    /**
     * Crée une map à partir d'une liste de String
     *
     * @param content liste de String correspondant chacune à une ligne
     * @return la map contenant les données, vide si la liste n'est pas valide
     */
    public Map<String, Object> parseFromString(List<String> content) {
        Map<String, Object> data = new HashMap<>();
        List<String[]> temp = new ArrayList<>();

        for (String line : content)
            temp.add(line.split(":"));

        for (String[] line : temp) {
            if (line.length == 2)
                data.put(line[0], line[1]);
        }

        return data;
    }
}
