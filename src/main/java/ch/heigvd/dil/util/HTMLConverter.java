package ch.heigvd.dil.util;


import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
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
     * Interdit la construction.
     */
    private HTMLConverter() {}

    /**
     * Converti le contenu markdown fourni en HTML.
     *
     * @param markdown le contenu en format markdown
     * @return le contenu converti en HTML
     */
    public static String fromMarkdown(String markdown) {
        Parser parser = Parser.builder().build();

        Node document = parser.parse(markdown);
        document.accept(new ImageVisitor());

        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

        return htmlRenderer.render(document);
    }

    /**
     * Visiteur utilisé pour supprimer les balises p des images.
     */
    private static class ImageVisitor extends AbstractVisitor {
        @Override
        public void visit(Image image) {
            var parent = image.getParent();
            var grandParent = parent.getParent();
            parent.unlink();
            grandParent.appendChild(image);
        }
    }
}
