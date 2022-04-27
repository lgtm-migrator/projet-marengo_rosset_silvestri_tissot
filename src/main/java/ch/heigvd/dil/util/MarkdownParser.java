package ch.heigvd.dil.util;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Classe permettant de parser un fichier markdown.
 *
 * @author Géraud Silvestri
 * @author Stéphane Marengo
 */
public class MarkdownParser {
    private static final String SEPARATOR = "----";

    /**
     * Parse le fichier en séparant la configuration YAML et le contenu markdown.
     *
     * @param path le chemin du fichier markdown à traiter
     * @return un tuple contenant les données YAML et le contenu markdown
     * @throws IOException si une erreur I/O survient ou que le YAML n'est pas valide
     */
    public static Tuple<Map<String, Object>, String> from(Path path) throws IOException {
        String content = Files.readString(path);

        String[] splitted = content.split(SEPARATOR);
        boolean isTwoPart = splitted.length == 2;

        Map<String, Object> config = null;
        try {
            config = new Yaml().load(splitted[0]);
        } catch (Exception e) {
            if (isTwoPart) throw new IOException("Error while parsing YAML configuration");
        }

        if (config == null) config = new LinkedHashMap<>();

        String markdown;
        if (isTwoPart) {
            markdown = splitted[1];
        } else {
            markdown = !config.isEmpty() ? "" : splitted[0];
        }

        return new Tuple<>(config, markdown.trim());
    }
}
