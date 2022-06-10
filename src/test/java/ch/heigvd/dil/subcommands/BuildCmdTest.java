package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.*;
import static picocli.CommandLine.ExitCode;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.*;
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
        // Redirection des flux
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        System.setIn(in);
        System.setOut(new PrintStream(out));

        Files.createDirectory(BUILD_PATH); // Nécessaire pour enregistrer le watcher

        var future = CompletableFuture.runAsync(() -> {
            execute(TEST_DIRECTORY.toString(), "--watch");
        });

        Files.writeString(TEST_DIRECTORY.resolve("test.md"), "contenu");
        awaitFile(BUILD_PATH.resolve("index.html"));
        System.out.println("exit");
        future.join();
        assertTrue(Files.exists(BUILD_PATH.resolve("test.html")));
    }

    /**
     * <a href="https://stackoverflow.com/a/57508242">Source</a>
     */
    private static void awaitFile(Path target) throws IOException, InterruptedException {
        final Path name = target.getFileName();
        final Path targetDir = target.getParent();

        // If path already exists, return early
        if (Files.exists(target)) return;

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            final WatchKey watchKey = targetDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            // The file could have been created in the window between Files.readAttributes and Path.register
            if (Files.exists(target)) return;

            // The file is absent: watch events in parent directory
            WatchKey watchKey1 = null;
            boolean valid = true;
            do {
                long t0 = System.currentTimeMillis();
                watchKey1 = watchService.take();
                // Examine events associated with key
                for (WatchEvent<?> event : watchKey1.pollEvents()) {
                    Path path1 = (Path) event.context();
                    if (path1.getFileName().equals(name)) {
                        return;
                    }
                }
                valid = watchKey1.reset();
            } while (valid);
        }
    }
}
