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
}
