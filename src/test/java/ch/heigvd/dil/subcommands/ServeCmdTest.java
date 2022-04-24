package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static picocli.CommandLine.ExitCode;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class ServeCmdTest extends BaseCmdTest {
    private static final String NOT_A_DIRECTORY = "notADirectory/";

    @Override
    protected String getCommandName() {
        return "serve";
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
    void itShouldThrowOnMissingBuild() throws IOException {
        cleanBuild();
        assertFalse(Files.isDirectory(BUILD_PATH));
        assertEquals(ExitCode.USAGE, execute(TEST_DIRECTORY.toString()));
    }

    // TODO ajouter des tests qui vérifient que le serveur est bien lancé
    // à voir comment faire avec le System.in
}
