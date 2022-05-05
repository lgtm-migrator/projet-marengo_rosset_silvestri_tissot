package ch.heigvd.dil.util;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Classe permettant d'injecter du contenu dans des pages HTML selon un système de template.
 *
 * @author Marengo Stéphane
 * @author Silvestri Géraud
 */
public class HTMLTemplater {
    private static final String LAYOUT_FILE = "layout";
    private final Map<String, Object> config;
    private final Template template;

    /**
     * Créé un templater utilisant la configuration et le dossier de template donné.
     * @param configFile le fichier de configuration du site
     * @param templateDir le dossier contenant les templates
     * @throws IOException si le fichier ou le dossier n'existe pas
     */
    public HTMLTemplater(Path configFile, Path templateDir) throws IOException {
        throwIfNotExists(configFile, "Config file does not exist");
        throwIfNotExists(templateDir, "Template directory does not exist");
        Path layoutFilePath = templateDir.resolve(LAYOUT_FILE + ".html");
        throwIfNotExists(layoutFilePath, "Layout file does not exist");

        config = new Yaml().load(Files.readString(configFile));
        Handlebars handlebars = new Handlebars(new FileTemplateLoader(templateDir.toFile(), ".html"));
        template = handlebars.compile(LAYOUT_FILE);
    }

    /**
     * Injecte les différentes valeurs dans la page donnée.
     * @param path la page traitée
     * @return le contenu de la page après injection
     * @throws IOException si le fichier n'existe pas ou qu'une erreur IO est survenue
     * @throws com.github.jknack.handlebars.HandlebarsException si une erreur de template est survenue
     */
    public String inject(Path path) throws IOException {
        throwIfNotExists(path, "Page does not exist: " + path);

        var page = MarkdownParser.from(path);
        var content = HTMLConverter.fromMarkdown(page.getSecond());

        Context context = Context.newBuilder(new Object())
                .combine("site", config)
                .combine("page", page.getFirst())
                .combine("content", content)
                .build();

        return template.apply(context);
    }

    /**
     * Lance une exception si le chemin spécifié n'existe pas.
     * @param path le chemin à vérifier
     * @param message le message d'erreur
     */
    private void throwIfNotExists(Path path, String message) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException(message);
        }
    }
}
