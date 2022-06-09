package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.*;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.FilesHelper;
import ch.heigvd.dil.util.HTMLTemplater;
import ch.heigvd.dil.util.TreeWatcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * @author Stéphane Marengo
 */
@Command(name = "build", description = "Builds the site")
public class BuildCmd implements Callable<Integer> {
    private static final String STOP_KEYWORD = "exit";
    public static final String BUILD_DIR = "build";
    private static final String TEMPLATE_DIR = "templates";
    private static final String CONFIG_FILE = "config.yaml";

    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path path;

    @Option(
            names = {"-w", "--watch"},
            description = "Watch for changes and rebuild the site",
            defaultValue = "false")
    private boolean isWatching;

    private TreeWatcher watcher;

    @Override
    public Integer call() {
        Path out = path.resolve(BUILD_DIR);
        try {
            FilesHelper.cleanDirectory(out);
        } catch (IOException e) {
            System.err.println("An error occurred while creating the directory: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        try {
            build(path, out);
        } catch (IOException e) {
            System.err.println("An error occurred while building the site: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        System.out.println("Site built successfully to " + out.toAbsolutePath());

        if (isWatching) {
            try {
                startWatching();
                waitForExit();
            } catch (IOException e) {
                System.err.println("An error occurred while watching for changes: " + e.getMessage());
                return ExitCode.SOFTWARE;
            }
        }

        return ExitCode.OK;
    }

    /**
     * Construit le site.
     * @param srcDir le dossier contenant les sources
     * @param destDir le dossier de destination
     * @throws IOException si une erreur IO survient
     */
    private void build(Path srcDir, Path destDir) throws IOException {
        HTMLTemplater templater = new HTMLTemplater(srcDir.resolve(CONFIG_FILE), srcDir.resolve(TEMPLATE_DIR));

        try (Stream<Path> walk = Files.walk(srcDir)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> !p.toString().endsWith(".yml"))
                    .filter(p -> !p.toString().endsWith(".yaml"))
                    .forEach(p -> {
                        var outPath = destDir.resolve(srcDir.relativize(p));
                        try {
                            buildFile(p, outPath, templater);
                        } catch (IOException e) {
                            System.err.println("An error occurred while copying the file: " + e.getMessage());
                        }
                    });
        }
    }

    /**
     * Copie le fichier spécifié et le convertit en HTML si nécessaire.
     * @param srcFile le fichier à copier
     * @param destFile le fichier de destination
     * @param templater le templater utilisé pour convertir le fichier
     * @throws IOException si une erreur IO survient
     */
    private void buildFile(Path srcFile, Path destFile, HTMLTemplater templater) throws IOException {
        if (srcFile.toString().endsWith(".md")) {
            convertMdToHTML(templater, srcFile, destFile);
        } else {
            Files.createDirectories(destFile.getParent());
            Files.copy(srcFile, destFile);
        }
    }

    /**
     * Converti le fichier markdown donné en HTML dans le dossier de build.
     * @param file le fichier markdown à convertir
     * @param dest le fichier de destination
     * @throws IOException si une erreur IO survient
     */
    private void convertMdToHTML(HTMLTemplater templater, Path file, Path dest) throws IOException {
        var htmlPath = dest.resolveSibling(dest.getFileName().toString().replace(".md", ".html"));

        Files.createDirectories(htmlPath.getParent());
        Files.writeString(htmlPath, templater.inject(file));
    }

    /**
     * Démarre le watcher.
     * @throws IOException si une erreur IO survient
     */
    private void startWatching() throws IOException {
        watcher = new TreeWatcher(
                path,
                paths -> {
                    System.out.println("Rebuilding site...");
                    try {
                        build(path, path.resolve(BUILD_DIR));
                    } catch (IOException e) {
                        System.err.println("An error occurred while building the site: " + e.getMessage());
                    }
                },
                path.resolve(BUILD_DIR));
        System.out.println("Watching for changes in " + path.toAbsolutePath());
        watcher.start();
    }

    /**
     * Boucle jusqu'à ce que l'utilisateur entre STOP_KEYWORD.
     * @throws IOException si une erreur IO survient
     */
    private void waitForExit() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNext(STOP_KEYWORD)) {
            scanner.nextLine();
        }
        watcher.stop();
    }
}
