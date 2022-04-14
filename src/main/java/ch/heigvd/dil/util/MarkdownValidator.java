package ch.heigvd.dil.util;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Mets à disposition une méthode permettant de séparer les données YAML et md d'un document
 * puis de les convertir en utilisant les parsers
 *
 * @author Géraud Silvestri
 */
public class MarkdownValidator {

    static final String SEPARATOR = "----";

    /**
     * Converti le fichier markdown passé en paramètre en HTML.
     *
     * @param path le chemin du fichier markdown à traiter
     * @return le contenu en HTML
     * @throws IOException si une erreur I/O survient
     */
    public static Tuple<Map<String, Object>, String> convertMarkdownFiles(String path) throws IOException {
        StringBuilder markdown = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                markdown.append(line).append("\r\n");
                line = reader.readLine();
            }
        }

        String[] datas = markdown.toString().split(SEPARATOR);
        return new Tuple(YAMLParser.parseFromString(datas[0]), HTMLConverter.convertMarkdownToHTML(datas[1]));
    }
}
