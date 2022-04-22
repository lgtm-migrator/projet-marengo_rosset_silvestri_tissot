package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class BuildCmdTest extends BaseCmdTest {
    private static final String INVALID_PATH = "/*invalidPath";
    private static final String NOT_A_DIRECTORY = "notADirectory/";
    private static final Path BUILD_DIR = TEST_DIRECTORY.resolve(BuildCmd.BUILD_DIR);

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
        execute(TEST_DIRECTORY.toString());
        assertTrue(Files.isDirectory(BUILD_DIR));
        assertEquals(0, getReturnCode());
    }

    @Test
    void itShouldBuildTheSite() {
        execute(TEST_DIRECTORY.toString());
        assertTrue(Files.exists(BUILD_DIR.resolve("index.html")));
        assertEquals(0, getReturnCode());
    }

    @Test
    void itShouldNotIncludeConfigFiles() {
        execute(TEST_DIRECTORY.toString());
        assertFalse(Files.exists(BUILD_DIR.resolve("config.yml")));
        assertEquals(0, getReturnCode());
    }

    @Test
    void itShouldCopyTheAssets() {
        execute(TEST_DIRECTORY.toString());
        assertTrue(Files.exists(BUILD_DIR.resolve(Path.of("images", "image.jpg"))));
        assertEquals(0, getReturnCode());
    }
}
