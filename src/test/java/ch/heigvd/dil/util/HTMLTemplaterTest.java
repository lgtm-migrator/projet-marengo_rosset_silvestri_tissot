package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.jknack.handlebars.HandlebarsException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 * @author Silvestri Géraud
 */
class HTMLTemplaterTest {
    private static final Path TEST_SRC_DIRECTORY = Path.of("src/test/resources/HTMLTemplater");
    private static final Path TEST_DIRECTORY = Path.of("HTMLTemplater");
    private static final Path CONFIG_FILE = TEST_DIRECTORY.resolve("config.yaml");
    private static final Path TEMPLATE_DIR = TEST_DIRECTORY.resolve("templates");
    private static final Path CONTENT_DIR = TEST_DIRECTORY.resolve("content");

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(TEST_DIRECTORY);
        FilesHelper.copyDirectory(TEST_SRC_DIRECTORY, TEST_DIRECTORY);
    }

    @AfterEach
    void clean() throws IOException {
        FilesHelper.cleanDirectory(TEST_DIRECTORY);
        Files.delete(TEST_DIRECTORY);
    }

    @Test
    void itShouldThrowOnMissingLayout() throws IOException {
        FilesHelper.cleanDirectory(TEMPLATE_DIR);
        assertThrows(IllegalArgumentException.class, () -> new HTMLTemplater(CONFIG_FILE, TEMPLATE_DIR));
    }

    @Test
    void itShouldThrowOnIncludeNotFound() throws IOException {
        Files.delete(TEMPLATE_DIR.resolve("menu.html"));
        assertThrows(HandlebarsException.class, () -> {
            HTMLTemplater templater = new HTMLTemplater(CONFIG_FILE, TEMPLATE_DIR);
            templater.inject(CONTENT_DIR.resolve("page.md"));
        });
    }

    @Test
    void itShouldInjectTheContent() throws IOException {
        HTMLTemplater templater = new HTMLTemplater(CONFIG_FILE, TEMPLATE_DIR);
        var actual = templater.inject(CONTENT_DIR.resolve("page.md"));
        var excepted = Files.readString(TEST_SRC_DIRECTORY.resolve("output/excepted.html"));
        assertEquals(excepted.replace("\r\n", "\n"), actual.replace("\r\n", "\n"));
    }
}
