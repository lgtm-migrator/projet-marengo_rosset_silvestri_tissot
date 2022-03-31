package ch.heigvd.dil;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Classe fournissant une méthode statique convertissant
 * le contenu d'un fichier markdown en contenu HTML.
 * @author Loïc Rosset
 */
public class HTMLConverter {
    /**
     * Fonction lisant un fichier markdown est convertissant son contenu en HTML.
     * @param path Chemin du fichier markdown à traiter
     * @return String contenant le contenu du fichier markdown converti en HTML
     * @throws IOException Fichier introuvable ou problème lors de la lecture de celui-ci
     */
    public static String convertMarkdownFiles(String path) throws IOException {
        
        StringBuilder markdown = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line = reader.readLine();

            while(line != null){
                markdown.append(line).append("\r\n");
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier est introuvable.");
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return convertMarkdownToHTML(markdown.toString());
    }

    /**
     * Converti un string au format Markdown en string au format HTML
     * @param markdown Contenu au format Markdown
     * @return Contenu au format HTML
     */
    private static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

        // Les balises img sont initialement encapsulées dans une balise <p>, il faut donc retirer ses balises <p>
        String[] lines = htmlRenderer.render(document).split("\n");
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < lines.length; i++){
            if(lines[i].contains("<img")){
                lines[i] = lines[i].replace("<p>", "");
                lines[i] = lines[i].replace("</p>", "");
            }

            result.append(lines[i]);

            // Ajout de retours à la ligne pour obtenir un résultat plus lisible
            if(i < lines.length - 1)
                result.append("\n\n");
        }

        return result.toString();
    }
}
