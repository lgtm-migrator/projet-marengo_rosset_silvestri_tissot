package ch.heigvd.dil.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author Loïc Rosset
 * @author Stéphane Marengo
 */
public class HTMLConverterTest {
    private static final String TEST_FILE = "src/test/resources/markdown_correct.md";

    @Test
    public void itShouldConvertMDtoHTML() throws IOException {
        String realConversion = "<h1>Mon premier article</h1>\n\n"
                + "<h2>Mon sous-titre</h2>\n\n"
                + "<p>Le contenu de mon article.</p>\n\n"
                + "<img src=\"./image.png\" alt=\"Une image\" />";

        assertEquals(HTMLConverter.convertMarkdownFiles(TEST_FILE), realConversion);
    }

    @Test
    public void itShouldThrowOnInvalidFile() {
        assertThrows(IOException.class, () -> HTMLConverter.convertMarkdownFiles("dummy.md"));
    }
}
