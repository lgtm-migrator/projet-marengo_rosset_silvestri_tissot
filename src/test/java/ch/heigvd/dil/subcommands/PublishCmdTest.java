package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static picocli.CommandLine.ExitCode;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class PublishCmdTest extends BaseCmdTest {
    private static final String NOT_A_DIRECTORY = "notADirectory/";
    private static final String INACCESSIBLE_REMOTE_PATH = "inaccessibleRemotePath/";
    private static final String REMOTE_PATH = "remotePath/";

    @BeforeEach
    protected void setUp() throws Exception {
        createBasicSite();
    }

    @AfterEach
    protected void clean() throws IOException {
        deleteBasicSite();
    }

    @Override
    protected String getCommandName() {
        return "publish";
    }

    @Test
    void itShouldThrowOnInvalidPath() {
        assertEquals(ExitCode.USAGE, execute(INVALID_PATH));
    }

    @Test
    void itShouldThrowOnInaccessibleRemotePath() {
        assertEquals(ExitCode.USAGE, execute(INACCESSIBLE_REMOTE_PATH));
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
    void itShouldUploadTheBuildDirectory() {
        assertEquals(ExitCode.OK, execute(REMOTE_PATH));
        assertTrue(Files.isDirectory(BUILD_PATH));
    }
}
