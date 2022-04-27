package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * @author Géraud Silvestri
 * @author Stéphane Marengo
 */
class MarkdownParserTest {
    private static final Path MARKDOWN_FOLDER = Path.of("src/test/resources/markdown");

    private static final String EXCEPTED_CONTENT = "# Mon premier article\n\n"
            + "## Mon sous-titre\n\n"
            + "Le contenu de mon article.\n\n"
            + "![Une image](./image.png)";

    @Test
    void itShouldParseCorrectly() throws IOException {
        var result = MarkdownParser.from(MARKDOWN_FOLDER.resolve("correct.md"));
        var config = result.getFirst();
        var content = result.getSecond();

        assertEquals(EXCEPTED_CONTENT, content.replace("\r\n", "\n"));
        assertEquals("Bob", config.get("author"));
        assertEquals("0.0.1", config.get("version"));
        assertEquals("today", config.get("date"));
    }

    @Test
    void itShouldThrowOnFileNotFound() {
        assertThrows(NoSuchFileException.class, () -> MarkdownParser.from(MARKDOWN_FOLDER.resolve("not_found.md")));
    }

    @Test
    void itShouldThrowOnMalformedYaml() {
        assertThrows(IOException.class, () -> MarkdownParser.from(MARKDOWN_FOLDER.resolve("incorrectYaml.md")));
    }

    @Test
    void itShouldParseCorrectlyWhenThereIsNoYaml() throws IOException {
        var result = MarkdownParser.from(MARKDOWN_FOLDER.resolve("mdOnly.md"));
        var result2 = MarkdownParser.from(MARKDOWN_FOLDER.resolve("mdOnlySep.md"));
        var config = result.getFirst();
        var config2 = result2.getFirst();
        var content = result.getSecond();
        var content2 = result2.getSecond();

        assertEquals(EXCEPTED_CONTENT, content.replace("\r\n", "\n"));
        assertEquals(EXCEPTED_CONTENT, content2.replace("\r\n", "\n"));
        assertTrue(config.isEmpty());
        assertTrue(config2.isEmpty());
    }

    @Test
    void itShouldParseCorrectlyWhenThereIsNoMarkdown() throws IOException {
        var result = MarkdownParser.from(MARKDOWN_FOLDER.resolve("yamlOnly.md"));
        var result2 = MarkdownParser.from(MARKDOWN_FOLDER.resolve("yamlOnlySep.md"));
        var config = result.getFirst();
        var config2 = result2.getFirst();
        var content = result.getSecond();
        var content2 = result2.getSecond();

        assertEquals("", content);
        assertEquals("", content2);
        assertEquals("Bob", config.get("author"));
        assertEquals("0.0.1", config.get("version"));
        assertEquals("today", config.get("date"));
        assertEquals("Bob", config2.get("author"));
        assertEquals("0.0.1", config2.get("version"));
        assertEquals("today", config2.get("date"));
    }

    @Test
    void itShouldNotThrowOnEmptyFile() throws IOException {
        var result = MarkdownParser.from(MARKDOWN_FOLDER.resolve("empty.md"));
        var config = result.getFirst();
        var content = result.getSecond();

        assertEquals("", content);
        assertTrue(config.isEmpty());
    }
}
