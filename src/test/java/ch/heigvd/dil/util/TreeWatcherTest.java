package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    /**
     * Interface utilisée pour définir une méthode pouvant throw une exception.
     */
    interface Tester {
        void run() throws Exception;
    }

    private static final Path ROOT_DIRECTORY = Path.of("rootTreeWatcher");
    private Path rootPath;
    private Path file;
    private Path nestedFile;
    private Path ignoredFile;
    private TreeWatcher tw;
    private Path exceptedFile;

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
                (added, modified, deleted) -> {
                    synchronized (this) {
                        var matched = Stream.of(added, modified, deleted).anyMatch(paths -> {
                            return Arrays.stream(paths).anyMatch(path -> path.equals(exceptedFile));
                        });
                        if (matched) {
                            notify();
                        }
                    }
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

    /**
     * Teste la notification suite à une action
     * @param exceptedFile le fichier qui doit être (ou non) notifié
     * @param tester l'action à exécuter
     * @throws Exception si une exception est levée
     */
    private synchronized void test(Path exceptedFile, Tester tester) throws Exception {
        this.exceptedFile = exceptedFile;
        tester.run();
        wait();
    }

    @Test
    void itShouldNotifyOnModifiedFile() {
        assertDoesNotThrow(() -> test(file, () -> Files.writeString(file, "modified")));
    }

    @Test
    void itShouldNotifyOnDeletedFile() {
        assertDoesNotThrow(() -> test(file, () -> Files.delete(file)));
    }

    @Test
    void itShouldNotifyOnAddedFile() {
        Path newFile = rootPath.resolve("newFile.txt");
        assertDoesNotThrow(() -> test(newFile, () -> Files.createFile(newFile)));
    }

    @Test
    void itShouldNotifyOnModifiedFileInSubDirectory() {
        assertDoesNotThrow(() -> test(nestedFile, () -> Files.writeString(nestedFile, "new content")));
    }

    @Test
    void itShouldNotCrashOnDoubleStart() {
        assertDoesNotThrow(() -> tw.start());
    }

    @Test
    void itShouldNotCrashOnDoubleStop() {
        assertDoesNotThrow(() -> {
            tw.stop();
            tw.stop();
        });
    }
}
