package ch.heigvd.dil.util;


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
     * Converti le contenu markdown fourni en HTML.
     *
     * @param markdown le contenu en format markdown
     * @return le contenu converti en HTML
     */
    public static String fromMarkdown(String markdown) {
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
