package ch.heigvd.dil.util;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Classe utilitaire pour convertir des fichiers markdowns en HTML.
 *
 * @author Loïc Rosset
 * @author Stéphane Marengo
 */
public class HTMLConverter {
    /**
     * Converti le fichier markdown passé en paramètre en HTML.
     *
     * @param path le chemin du fichier markdown à traiter
     * @return le contenu en HTML
     * @throws IOException si une erreur I/O survient
     */
    public static String convertMarkdownFiles(String path) throws IOException {
        StringBuilder markdown = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                markdown.append(line).append("\r\n");
                line = reader.readLine();
            }
        }

        return convertMarkdownToHTML(markdown.toString());
    }

    /**
     * Converti le contenu markdown fourni en HTML.
     *
     * @param markdown le contenu en format markdown
     * @return le contenu converti en HTML
     */
    private static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

        // Les balises img sont initialement encapsulées dans une balise <p>, il faut donc retirer ces balises <p>
        String[] lines = htmlRenderer.render(document).split("\n");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("<img")) {
                lines[i] = lines[i].replace("<p>", "");
                lines[i] = lines[i].replace("</p>", "");
            }

            result.append(lines[i]);

            // Ajout de retours à la ligne pour obtenir un résultat plus lisible
            if (i < lines.length - 1) result.append("\n\n");
        }

        return result.toString();
    }
}
