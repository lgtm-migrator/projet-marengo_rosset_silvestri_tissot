package ch.heigvd.dil;

import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HTMLConverterTest {
    /**
     * Test sur la conversion d'un markdown en html.
     * Le fichier markdown utilisé est correctement formaté.
     */
    @Test
    public void shouldReturnHTMLContent() {
        String correctPath = "src/test/resources/markdown_correct.md";
        String realConversion = "<h1>Mon premier article</h1>\n\n"
                + "<h2>Mon sous-titre</h2>\n\n"
                + "<p>Le contenu de mon article.</p>\n\n"
                + "<img src=\"./image.png\" alt=\"Une image\" />";


        try{
            // TODO pas de </p>
            String s = HTMLConverter.convertMarkdownFiles(correctPath);
            if(Objects.equals(HTMLConverter.convertMarkdownFiles(correctPath), realConversion)){
                assertTrue(true);
            }
            else{
                fail();
            }
        }
        catch(IOException e){
            fail();
        }
    }
}
