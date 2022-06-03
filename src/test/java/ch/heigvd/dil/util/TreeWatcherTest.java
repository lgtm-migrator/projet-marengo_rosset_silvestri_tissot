package ch.heigvd.dil.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marengo Stéphane
 */
class TreeWatcherTest {
    private static final Path ROOT_DIRECTORY = Path.of("rootTreeWatcher");
    private Path rootPath;
    private Path file;
    private Path nestedFile;
    private Path ignoredFile;
    private TreeWatcher tw;

    private boolean notified = false;

    private Path expectedFileName;

    @BeforeEach
    private void setUp() throws IOException {
        rootPath = Files.createDirectories(ROOT_DIRECTORY);
        file = Files.createFile(rootPath.resolve("file1.txt"));
        var dir = Files.createDirectory(rootPath.resolve("dir1"));
        nestedFile = Files.createFile(dir.resolve("file2.txt"));
        var ignored = Files.createDirectory(rootPath.resolve("ignored"));
        ignoredFile = Files.createFile(ignored.resolve("file3.txt"));

        tw = new TreeWatcher(
                rootPath,
                paths -> {
                    if (Arrays.stream(paths).anyMatch(p -> p.equals(expectedFileName))) notified = true;
                },
                ignored);
        tw.start();
    }

    @AfterEach
    void cleanUp() throws IOException {
        FilesHelper.cleanDirectory(rootPath);
        Files.delete(rootPath);
        tw.stop();
    }

    @Test
    void itShouldNotifyOnModifiedFile() throws IOException {
        expectedFileName = file.getFileName();
        Files.writeString(file, "new content");
        assertTrue(notified);
    }

    @Test
    void itShouldNotifyOnDeletedFile() throws IOException {
        expectedFileName = file.getFileName();
        Files.delete(file);
        assertTrue(notified);
    }

    @Test
    void itShouldNotifyOnAddedFile() throws IOException {
        var file = Files.createTempFile(rootPath, "", "");
        expectedFileName = file.getFileName();
        assertTrue(notified);
    }

    @Test
    void itShouldNotifyOnModifiedFileInSubDirectory() throws IOException {
        expectedFileName = nestedFile.getFileName();
        Files.writeString(nestedFile, "new content");
        assertTrue(notified);
    }

    @Test
    void itShouldNotNotifyIgnoredPaths() throws IOException {
        Files.writeString(ignoredFile, "new content");
        assertFalse(notified);
    }
}
