package ch.heigvd.dil.util;


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
                paths -> {
                    if (Arrays.stream(paths).noneMatch(path -> path.equals(exceptedFile))) return;
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
     * @throws Exception si une exception est levée
     */
    private void test(Path exceptedFile, Tester tester) throws Exception {
        this.exceptedFile = exceptedFile;

        synchronized (this) {
            tester.run();
            wait();
        }
    }

    @Test
    void itShouldNotifyOnModifiedFile() throws Exception {
        test(file, () -> Files.writeString(file, "modified"));
    }

    @Test
    void itShouldNotifyOnDeletedFile() throws Exception {
        test(file, () -> Files.delete(file));
    }

    @Test
    void itShouldNotifyOnAddedFile() throws Exception {
        Path newFile = rootPath.resolve("newFile.txt");
        test(newFile, () -> Files.createFile(newFile));
    }

    @Test
    void itShouldNotifyOnModifiedFileInSubDirectory() throws Exception {
        test(nestedFile, () -> Files.writeString(nestedFile, "new content"));
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
