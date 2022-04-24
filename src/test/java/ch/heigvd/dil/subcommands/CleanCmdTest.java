package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.*;
import static picocli.CommandLine.ExitCode;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Tissot Olivier
 * @author Stéphane Marengo
 */
class CleanCmdTest extends BaseCmdTest {
    @Override
    protected String getCommandName() {
        return "clean";
    }

    @BeforeEach
    protected void setUp() throws IOException {
        buildBasicSite();
    }

    @AfterEach
    protected void clean() throws IOException {
        deleteBasicSite();
    }

    @Test
    void itShouldDeleteTheBuildDirAndItsContent() {
        assertTrue(Files.isDirectory(BUILD_PATH));
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
        assertFalse(Files.isDirectory(BUILD_PATH));
    }

    @Test
    void itShouldNotThrowIfBuildDirDoesNotExist() throws IOException {
        cleanBuild();
        assertFalse(Files.isDirectory(BUILD_PATH));
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
    }
}
