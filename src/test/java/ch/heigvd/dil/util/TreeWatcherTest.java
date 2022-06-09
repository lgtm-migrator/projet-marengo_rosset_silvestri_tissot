package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
                paths -> {
                    notified = Arrays.stream(paths).anyMatch(path -> path.equals(exceptedFile));
                    synchronized (this) {
                        notify();
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
     * @param exceptedNotified le résultat attendu (notifié ou non)
     * @throws Exception si une exception est levée
     */
    private void test(Path exceptedFile, Tester tester, boolean exceptedNotified) throws Exception {
        this.exceptedFile = exceptedFile;
        tester.run();
        synchronized (this) {
            wait();
        }
        assertEquals(exceptedNotified, notified);
    }

    @Test
    void itShouldNotifyOnModifiedFile() throws Exception {
        test(file, () -> Files.writeString(file, "modified"), true);
    }

    @Test
    void itShouldNotifyOnDeletedFile() throws Exception {
        test(file, () -> Files.delete(file), true);
    }

    @Test
    void itShouldNotifyOnAddedFile() throws Exception {
        Path newFile = rootPath.resolve("newFile.txt");
        test(newFile, () -> Files.createFile(newFile), true);
    }

    @Test
    void itShouldNotifyOnModifiedFileInSubDirectory() throws Exception {
        test(nestedFile, () -> Files.writeString(nestedFile, "new content"), true);
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
}
