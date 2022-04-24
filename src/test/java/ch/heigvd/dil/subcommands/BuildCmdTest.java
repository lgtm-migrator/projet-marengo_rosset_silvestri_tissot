package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.*;
import static picocli.CommandLine.ExitCode;

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
        assertEquals(ExitCode.USAGE, execute(INVALID_PATH));
    }

    @Test
    void itShouldThrowOnNotADirectory() {
        assertEquals(ExitCode.USAGE, execute(NOT_A_DIRECTORY));
    }

    @Test
    void itShouldThrowOnMissingPath() {
        assertEquals(ExitCode.USAGE, execute());
    }

    @Test
    void itShouldCreateTheBuildDirectory() {
        execute(TEST_DIRECTORY.toString());
        assertTrue(Files.isDirectory(BUILD_DIR));
        assertEquals(ExitCode.OK, getReturnCode());
    }

    @Test
    void itShouldBuildTheSite() {
        execute(TEST_DIRECTORY.toString());
        assertTrue(Files.exists(BUILD_DIR.resolve("index.html")));
        assertEquals(ExitCode.OK, getReturnCode());
    }

    @Test
    void itShouldNotIncludeConfigFiles() {
        execute(TEST_DIRECTORY.toString());
        assertFalse(Files.exists(BUILD_DIR.resolve("config.yml")));
        assertEquals(ExitCode.OK, getReturnCode());
    }

    @Test
    void itShouldCopyTheAssets() {
        execute(TEST_DIRECTORY.toString());
        assertTrue(Files.exists(BUILD_DIR.resolve(Path.of("images", "image.jpg"))));
        assertEquals(ExitCode.OK, getReturnCode());
    }
}
