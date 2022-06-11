package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.*;
import static picocli.CommandLine.ExitCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class BuildCmdTest extends BaseCmdTest {
    private static final String NOT_A_DIRECTORY = "notADirectory/";

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
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
        assertTrue(Files.isDirectory(BUILD_PATH));
    }

    @Test
    void itShouldBuildTheSite() throws IOException {
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
        assertTrue(Files.exists(BUILD_PATH.resolve("index.html")));
        assertEquals(
                Files.readString(BUILD_SRC_DIRECTORY.resolve("index.html")).replace("\r\n", "\n"),
                Files.readString(BUILD_PATH.resolve("index.html")).replace("\r\n", "\n"));
    }

    @Test
    void itShouldNotIncludeConfigFiles() {
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
        assertFalse(Files.exists(BUILD_PATH.resolve("config.yaml")));
    }

    @Test
    void itShouldCopyTheAssets() {
        assertEquals(ExitCode.OK, execute(TEST_DIRECTORY.toString()));
        assertTrue(Files.exists(BUILD_PATH.resolve(Path.of("images", "image.jpg"))));
    }

    @Test
    void itShouldRebuildWhenWatching() throws Exception {
        var outs = redirectIO();
        var out = outs.getFirst();
        var reader = outs.getSecond();

        var future = CompletableFuture.supplyAsync(() -> {
            return execute(TEST_DIRECTORY.toString(), "--watch");
        });

        while (!reader.toString().contains(ServeCmd.STOP_KEYWORD)) {
            Thread.sleep(1000);
        }

        Files.writeString(TEST_DIRECTORY.resolve("test.md"), "contenu");
        awaitFile(BUILD_PATH.resolve("test.html"));
        out.println(ServeCmd.STOP_KEYWORD);
        assertEquals(ExitCode.OK, future.join());
        resetIO();
    }
}
