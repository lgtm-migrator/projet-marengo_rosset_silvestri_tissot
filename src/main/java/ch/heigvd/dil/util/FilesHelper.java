package ch.heigvd.dil.util;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Fournit des méthodes utilitaires pour manipuler les fichiers.
 *
 * @author Marengo Stéphane
 */
public class FilesHelper {
    /**
     * Interdit la construction.
     */
    private FilesHelper() {}
    /**
     * Supprime le contenu du répertoire spécifié.
     * Ce dernier est créé s'il n'existe pas.
     *
     * @param directory le répertoire à vider
     * @throws IOException si une erreur IO survient
     */
    public static void cleanDirectory(Path directory) throws IOException {
        Files.createDirectories(directory);

        try (Stream<Path> walk = Files.walk(directory)) {
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(file -> {
                if (directory.compareTo(file.toPath()) == 0) return;
                file.delete();
            });
        }
    }

    /**
     * Copie un répertoire.
     * @param src le répertoire source
     * @param dst le répertoire de destination
     * @throws IOException si une erreur survient lors de la copie
     */
    public static void copyDirectory(Path src, Path dst) throws IOException {
        try (Stream<Path> walk = Files.walk(src)) {
            walk.filter(Files::isRegularFile).forEach(p -> {
                Path dest = dst.resolve(src.relativize(p));
                try {
                    Files.createDirectories(dest.getParent());
                    Files.copy(p, dest);
                } catch (IOException e) {
                    System.err.println("Error while copying " + p + " to " + dest + ": " + e.getMessage());
                }
            });
        }
    }
}
