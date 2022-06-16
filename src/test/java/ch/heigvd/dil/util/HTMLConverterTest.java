package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * @author Loïc Rosset
 * @author Stéphane Marengo
 */
class HTMLConverterTest {
    private static final Path TEST_FILE = Path.of("src/test/resources/markdown_correct.md");

    @Test
    void itShouldConvertMDtoHTML() throws IOException {
        String realConversion = "<h1>Mon premier article</h1>\n"
                + "<h2>Mon sous-titre</h2>\n"
                + "<p>Le contenu de mon article.</p>\n"
                + "<img src=\"./image.png\" alt=\"Une image\" />";

        assertEquals(realConversion, HTMLConverter.fromMarkdown(Files.readString(TEST_FILE)));
    }
}
