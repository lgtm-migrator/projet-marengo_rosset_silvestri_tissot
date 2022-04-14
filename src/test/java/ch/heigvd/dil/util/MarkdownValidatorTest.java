package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author Géraud Silvestri
 */
class MarkdownValidatorTest {
    final String PATH = "src/test/resources/validator.txt";
    final String WRONG_FILE = "src/test/resources/dummy.txt";
    final String REAL_CONVERSION = "<h1>Mon premier article</h1>\n\n"
            + "<h2>Mon sous-titre</h2>\n\n"
            + "<p>Le contenu de mon article.</p>\n\n"
            + "<img src=\"./image.png\" alt=\"Une image\" />";

    @Test
    public void itShouldParseCorrectly() throws IOException {
        Tuple<Map<String, Object>, String> data = MarkdownValidator.convertMarkdownFiles(PATH);

        assertEquals(data.getSecond(), REAL_CONVERSION);
        assertEquals(data.getFirst().get("author"), "Bob");
        assertEquals(data.getFirst().get("version"), "0.0.1");
        assertEquals(data.getFirst().get("date"), "today");
    }

    @Test
    public void itShouldThrowOnFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> MarkdownValidator.convertMarkdownFiles(WRONG_FILE));
    }
}
