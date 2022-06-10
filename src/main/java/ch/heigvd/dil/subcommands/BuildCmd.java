package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.*;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.FilesHelper;
import ch.heigvd.dil.util.SiteBuilder;
import ch.heigvd.dil.util.TreeWatcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * @author Stéphane Marengo
 */
@Command(name = "build", description = "Builds the site")
public class BuildCmd implements Callable<Integer> {
    static final String STOP_KEYWORD = "exit";

    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path srcPath;

    @Option(
            names = {"-w", "--watch"},
            description = "Watch for changes and rebuild the site",
            defaultValue = "false")
    private boolean isWatching;

    private TreeWatcher watcher;

    private SiteBuilder siteBuilder;

    @Override
    public Integer call() {
        try {
            siteBuilder = new SiteBuilder(srcPath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return ExitCode.SOFTWARE;
        }

        if (siteBuilder.build()) {
            System.out.println(
                    "Site built successfully to " + siteBuilder.getBuildPath().toAbsolutePath());
        } else {
            return ExitCode.SOFTWARE;
        }

        if (isWatching) {
            try {
                startWatching();
                System.out.println("Watching for changes in " + srcPath.toAbsolutePath());
                System.out.println("Type '" + STOP_KEYWORD + "' to stop.");
                waitForExit();
            } catch (IOException e) {
                System.err.println("An error occurred while watching for changes: " + e.getMessage());
                return ExitCode.SOFTWARE;
            }
        }

        return ExitCode.OK;
    }

    /**
     * Démarre le watcher.
     * @throws IOException si une erreur IO survient
     */
    private void startWatching() throws IOException {
        watcher = new TreeWatcher(
                srcPath, this::watcherHandler, siteBuilder.getBuildPath(), siteBuilder.getTemplatePath());
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
            siteBuilder.buildFile(p);
        }
        for (Path p : modified) {
            siteBuilder.buildFile(p);
        }
        for (Path p : deleted) {
            var outPath = siteBuilder.getOutputpath(p);
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
