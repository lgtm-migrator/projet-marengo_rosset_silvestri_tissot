package ch.heigvd.dil.subcommands;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author Tissot Olivier
 * @author Stéphane Marengo
 */
@Command(name = "clean", description = "Subcommand clean !")
public class CleanCmd implements Callable<Integer> {
    private static final String BUILD = "build";

    @CommandLine.Parameters(arity = "1", description = "Chemin du site dont il faut supprimer le build")
    String path;

    @Override
    public Integer call() {
        System.out.println("Suppression du dossier build de " + path);

        Path pathComplet = Paths.get(path, BUILD);
        if (!Files.exists(pathComplet)) return 0;

        try (Stream<Path> walk = Files.walk(pathComplet)) {
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            System.out.println("Impossible");
            return -1;
        }

        return 0;
    }
}
