package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.*;

import ch.heigvd.dil.util.FilesHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class BuildCmdTest extends BaseCmdTest {
    private static final String INVALID_PATH = "/*invalidPath";
    private static final String NOT_A_DIRECTORY = "notADirectory/";
    private static final String TEST_PATH = "buildTest/";

    @BeforeAll
    static void setUpDirectory() throws IOException {
        Files.createDirectory(Path.of(TEST_PATH));

        copyDirectory(Path.of("src/test/resources/buildTest/"), Path.of(TEST_PATH));
    }

    private static void copyDirectory(Path src, Path dst) throws IOException {
        try (Stream<Path> walk = Files.walk(src)) {
            walk.filter(Files::isRegularFile).forEach(p -> {
                Path dest = dst.resolve(src.relativize(p));
                try {
                    Files.createDirectories(dest.getParent());
                    Files.copy(p, dest);
                } catch (IOException e) {
                    System.err.println("Error while copying " + p + " to " + dest + ": " + e.getMessage());
                }
            });
        }
    }

    @AfterAll
    static void cleanUpDirectory() throws IOException {
        Path path = Path.of(TEST_PATH);
        FilesHelper.cleanDirectory(path);
        Files.delete(path);
    }

    @Override
    protected String getCommandName() {
        return "build";
    }

    @Test
    void itShouldThrowOnInvalidPath() {
        assertEquals(2, execute(INVALID_PATH));
    }

    @Test
    void itShouldThrowOnNotADirectory() {
        assertEquals(2, execute(NOT_A_DIRECTORY));
    }

    @Test
    void itShouldThrowOnMissingPath() {
        assertEquals(2, execute());
    }

    @Test
    void itShouldCreateTheBuildDirectory() {
        execute(TEST_PATH);
        assertTrue(Files.isDirectory(Path.of(TEST_PATH, "build")));
        assertEquals(0, getReturnCode());
    }

    @Test
    void itShouldBuildTheSite() {
        execute(TEST_PATH);
        assertTrue(Files.exists(Path.of(TEST_PATH, "build", "index.html")));
        assertEquals(0, getReturnCode());
    }

    @Test
    void itShouldNotIncludeConfigFiles() {
        execute(TEST_PATH);
        assertFalse(Files.exists(Path.of(TEST_PATH, "build", "config.yml")));
        assertEquals(0, getReturnCode());
    }

    @Test
    void itShouldCopyTheAssets() {
        execute(TEST_PATH);
        assertTrue(Files.exists(Path.of(TEST_PATH, "build", "images", "image.jpg")));
        assertEquals(0, getReturnCode());
    }
}
