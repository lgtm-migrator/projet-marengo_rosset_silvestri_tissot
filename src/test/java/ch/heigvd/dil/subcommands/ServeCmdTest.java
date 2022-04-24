package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static picocli.CommandLine.ExitCode;

import ch.heigvd.dil.util.FilesHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class ServeCmdTest extends BaseCmdTest {
    private static final String INVALID_PATH = "/*invalidPath";
    private static final String NOT_A_DIRECTORY = "notADirectory/";
    private static final Path BUILD_PATH = TEST_DIRECTORY.resolve(BuildCmd.BUILD_DIR);

    /**
     * Construit le site.
     */
    private void buildSite() {
        // TODO ne devrait pas dépendre d'un autre test
        executeCmd("build", TEST_DIRECTORY.toString());
        assertEquals(ExitCode.OK, getReturnCode());
    }

    /**
     * Supprime le dossier de build s'il existe.
     * @throws IOException si un problème survient lors de la suppression
     */
    @AfterEach
    private void cleanBuild() throws IOException {
        if (!Files.isDirectory(BUILD_PATH)) return;

        FilesHelper.cleanDirectory(BUILD_PATH);
        Files.delete(BUILD_PATH);
    }

    @Override
    protected String getCommandName() {
        return "serve";
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
    void itShouldThrowOnMissingBuild() {
        assertEquals(ExitCode.USAGE, execute(TEST_DIRECTORY.toString()));
    }

    // TODO ajouter des tests qui vérifient que le serveur est bien lancé
    // à voir comment faire avec le System.in
}
