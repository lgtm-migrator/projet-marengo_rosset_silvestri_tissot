package ch.heigvd.dil.util;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        StringBuilder markdown = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line = reader.readLine();

            while (line != null) {
                markdown.append(line).append("\r\n");
                line = reader.readLine();
            }
        }

        assertEquals(HTMLConverter.convertMarkdownToHTML(markdown.toString()), realConversion);
    }
}
