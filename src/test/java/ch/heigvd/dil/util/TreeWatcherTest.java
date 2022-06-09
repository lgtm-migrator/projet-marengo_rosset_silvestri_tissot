package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class TreeWatcherTest {
    private static final int SLEEP_TIME = 10000;
    private static final Path ROOT_DIRECTORY = Path.of("rootTreeWatcher");
    private Path rootPath;
    private Path file;
    private Path nestedFile;
    private Path ignoredFile;
    private TreeWatcher tw;

    private boolean notified;

    private Path exceptedFile;

    @BeforeEach
    private void setUp() throws IOException {
        rootPath = Files.createDirectories(ROOT_DIRECTORY);
        file = Files.createFile(rootPath.resolve("file1.txt"));
        var dir = Files.createDirectory(rootPath.resolve("dir1"));
        nestedFile = Files.createFile(dir.resolve("file2.txt"));
        var ignored = Files.createDirectory(rootPath.resolve("ignored"));
        ignoredFile = Files.createFile(ignored.resolve("file3.txt"));

        notified = false;
        tw = new TreeWatcher(
                rootPath,
                (added, modified, deleted) -> {
                    notified = Stream.of(added, modified, deleted).anyMatch(paths -> {
                        return Arrays.stream(paths).anyMatch(path -> path.equals(exceptedFile));
                    });
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
    void itShouldNotifyOnModifiedFile() throws Exception {
        exceptedFile = file;
        Files.writeString(file, "new content");
        sleep();
        assertNotified(true);
    }

    @Test
    void itShouldNotifyOnDeletedFile() throws Exception {
        exceptedFile = file;
        Files.delete(file);
        sleep();
        assertNotified(true);
    }

    @Test
    void itShouldNotifyOnAddedFile() throws Exception {
        exceptedFile = Files.createTempFile(rootPath, "", "");
        sleep();
        assertNotified(true);
    }

    @Test
    void itShouldNotifyOnModifiedFileInSubDirectory() throws Exception {
        exceptedFile = nestedFile;
        Files.writeString(nestedFile, "new content");
        sleep();
        assertNotified(true);
    }

    @Test
    void itShouldNotNotifyIgnoredPaths() throws Exception {
        exceptedFile = ignoredFile;
        Files.writeString(ignoredFile, "new content");
        sleep();
        assertNotified(false);
    }

    @Test
    void itShouldNotCrashOnDoubleStart() throws Exception {
        tw.start();
    }

    @Test
    void itShouldNotCrashOnDoubleStop() throws Exception {
        tw.stop();
        tw.stop();
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
    }

    private void assertNotified(boolean expected) throws InterruptedException {
        sleep();
        assertEquals(expected, notified);
    }
}
