package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.*;
import static picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class InitCmdTest extends BaseCmdTest {
    @Override
    protected String getCommandName() {
        return "init";
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
    void itShouldThrowOnMissingPath() {
        assertEquals(ExitCode.USAGE, execute());
    }

    @Test
    void itShouldCreateTheDirectory() {
        assertFalse(Files.isDirectory(TEST_DIRECTORY));
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
        assertTrue(Files.isDirectory(TEST_DIRECTORY));
    }

    @Test
    void itShouldCreateTheDefaultSite() {
        assertFalse(Files.isDirectory(TEST_DIRECTORY));
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
        assertTrue(Files.isRegularFile(TEST_DIRECTORY.resolve("index.md")));
        assertTrue(Files.isRegularFile(TEST_DIRECTORY.resolve("config.yaml")));
    }

    @Test
    void itShouldNotOverwriteExistingConfig() throws IOException {
        createBasicSite();
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));

        var configFile = TEST_DIRECTORY.resolve("config.yaml");

        assertTrue(Files.isRegularFile(configFile));

        String basicSiteContent = Files.readString(configFile);
        String defaultContent = Files.readString(MAIN_RESOURCES.resolve("config.default.yaml"));

        assertNotEquals(basicSiteContent, defaultContent);
        assertEquals(Files.readString(TEST_SRC_DIRECTORY.resolve("config.yaml")), basicSiteContent);
    }

    @Test
    void itShouldNotOverwriteExistingIndex() throws IOException {
        createBasicSite();
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));

        var indexFile = TEST_DIRECTORY.resolve("index.md");

        assertTrue(Files.isRegularFile(indexFile));

        String basicSiteContent = Files.readString(indexFile);
        String defaultContent = Files.readString(MAIN_RESOURCES.resolve("index.default.md"));

        assertNotEquals(basicSiteContent, defaultContent);
        assertEquals(Files.readString(TEST_SRC_DIRECTORY.resolve("index.md")), basicSiteContent);
    }
}