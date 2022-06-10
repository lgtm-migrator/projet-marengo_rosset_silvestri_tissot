package ch.heigvd.dil.subcommands;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.FilesHelper;
import ch.heigvd.dil.util.SiteBuilder;
import ch.heigvd.dil.util.TreeWatcher;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.ScopeType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Commande de base permettant de construire un site. Ce dernier peut également être lancé
 * en mode watch, ce qui aura pour effet de le reconstruire en cas de changements.
 * @author Marengo Stéphane
 */
@Command(scope = ScopeType.INHERIT)
public abstract class BuildableCmd implements Callable<Integer> {
    static final String STOP_KEYWORD = "exit";

    @CommandLine.Option(
            names = {"-w", "--watch"},
            description = "Watch for changes and rebuild the site",
            defaultValue = "false")
    private boolean isWatching;

    @CommandLine.Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path srcPath;

    private TreeWatcher watcher;

    private SiteBuilder siteBuilder;

    /**
     * Créé le SiteBuilder.
     * @return ExitCode.OK si tout s'est bien passé, ExitCode.SOFTWARE sinon.
     */
    @Override
    public Integer call() {
        try {
            siteBuilder = new SiteBuilder(srcPath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return ExitCode.SOFTWARE;
        }

        return ExitCode.OK;
    }

    /**
     * Construit le site.
     * @return vrai si tout s'est bien passé, faux sinon.
     */
    protected boolean build() {
        if (siteBuilder.build()) {
            System.out.println(
                    "Site built successfully to " + siteBuilder.getBuildPath().toAbsolutePath());
        } else {
            return false;
        }
        return true;
    }

    /**
     * Détermine si l'option watch est activée.
     * @return vrai si l'option watch est activée, faux sinon.
     */
    protected boolean withWatcher() {
        return isWatching;
    }

    /**
     * Démarre le watcher et affiche un message d'information.
     * Une erreur est affichée si le watcher n'a pas pu être lancé.
     *
     * @return vrai si le watcher a été démarré, faux sinon.
     */
    protected boolean startWatching() {
        watcher = new TreeWatcher(
                srcPath, this::watcherHandler, siteBuilder.getBuildPath(), siteBuilder.getTemplatePath());
        try {
            watcher.start();
            System.out.println("Watching for changes in " + srcPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("An error occurred while watching for changes: " + e.getMessage());
            stopWatching();
            return false;
        }
        return true;
    }

    /**
     * Arrête le watcher.
     * Une erreur est affichée si le watcher n'a pas pu être arrêté correctement.
     *
     * @return vrai si le watcher a pu être arrêté, faux sinon.
     */
    protected boolean stopWatching() {
        try {
            watcher.stop();
        } catch (IOException e) {
            System.err.println("An error occurred while stopping the watcher: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Boucle jusqu'à ce que l'utilisateur entre STOP_KEYWORD.
     */
    protected void waitForExit() {
        System.out.println("Type '" + STOP_KEYWORD + "' to quit.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNext(STOP_KEYWORD)) {
            scanner.nextLine();
        }
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
}
