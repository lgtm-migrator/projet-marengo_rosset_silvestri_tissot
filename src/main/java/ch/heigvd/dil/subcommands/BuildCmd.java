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
    static final String TEMPLATE_DIR = "templates";
    static final String CONFIG_FILE = "config.yaml";

    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path srcPath;

    private Path outPath;

    private HTMLTemplater templater;

    @Option(
            names = {"-w", "--watch"},
            description = "Watch for changes and rebuild the site",
            defaultValue = "false")
    private boolean isWatching;

    private TreeWatcher watcher;

    @Override
    public Integer call() {
        outPath = srcPath.resolve(BUILD_DIR);
        try {
            FilesHelper.cleanDirectory(outPath);
        } catch (IOException e) {
            System.err.println("An error occurred while creating the directory: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        try {
            build();
        } catch (IOException e) {
            System.err.println("An error occurred while building the site: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        System.out.println("Site built successfully to " + outPath.toAbsolutePath());

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
     *
     * @throws IOException si une erreur IO survient
     */
    private void build() throws IOException {
        templater = new HTMLTemplater(srcPath.resolve(CONFIG_FILE), srcPath.resolve(TEMPLATE_DIR));

        try (Stream<Path> walk = Files.walk(srcPath)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> !p.toString().endsWith(".yml"))
                    .filter(p -> !p.toString().endsWith(".yaml"))
                    .forEach(this::buildFile);
        }
    }

    /**
     * Obtient le chemin de destination du fichier donné.
     * @param file le fichier
     * @return le chemin de destination
     */
    private Path getOutputpath(Path file) {
        return outPath.resolve(srcPath.relativize(file));
    }

    /**
     * Copie le fichier spécifié et le convertit en HTML si nécessaire.
     * @param srcFile le fichier à copier
     */
    private void buildFile(Path srcFile) {
        var outPath = getOutputpath(srcFile);

        try {
            if (srcFile.toString().endsWith(".md")) {
                convertMdToHTML(templater, srcFile, outPath);
            } else {
                Files.createDirectories(outPath.getParent());
                Files.copy(srcFile, outPath);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while copying the file: " + e.getMessage());
        }
    }

    /**
     * Converti le fichier markdown donné en HTML dans le dossier de build.
     *
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
        watcher = new TreeWatcher(srcPath, this::watcherHandler, srcPath.resolve(BUILD_DIR));
        System.out.println("Watching for changes in " + srcPath.toAbsolutePath());
        watcher.start();
    }

    /**
     * Handler pour le watcher.
     * @param added les fichiers ajoutés
     * @param modified les fichiers modifiés
     * @param deleted les fichiers supprimés
     */
    private void watcherHandler(Path[] added, Path[] modified, Path[] deleted) {
        System.out.println("Rebuilding site...");

        for (Path p : added) {
            buildFile(p);
        }
        for (Path p : modified) {
            buildFile(p);
        }
        for (Path p : deleted) {
            var outPath = getOutputpath(p);
            try {
                if (Files.isDirectory(p)) {
                    FilesHelper.cleanDirectory(outPath);
                }
                Files.delete(outPath);
            } catch (IOException e) {
                System.err.println("An error occurred while deleting the file: " + e.getMessage());
            }
        }
        System.out.println("...done");
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
