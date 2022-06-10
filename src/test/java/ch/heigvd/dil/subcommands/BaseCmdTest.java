package ch.heigvd.dil.subcommands;


import ch.heigvd.dil.Site;
import ch.heigvd.dil.util.FilesHelper;
import java.io.*;
import java.nio.file.*;
import picocli.CommandLine;

/**
 * Classe de base permettant de tester les sous-commandes.
 *
 * @author Marengo Stéphane
 * @author Silvestri Géraud
 */
abstract class BaseCmdTest {
    protected static final Path MAIN_RESOURCES = Path.of("src/main/resources");
    protected static final Path TEST_SRC_DIRECTORY = Path.of("src/test/resources/basicSite");
    protected static final Path BUILD_SRC_DIRECTORY = Path.of("src/test/resources/basicSiteBuild");
    protected static final Path TEST_DIRECTORY = Path.of("basicSite");
    protected static final Path BUILD_PATH = TEST_DIRECTORY.resolve(BuildableCmd.BUILD_DIR);
    protected static final String INVALID_PATH = "/\0invalidPath";

    private InputStream in;
    private PrintStream out;

    /**
     * Retourne le nom de la commande à tester.
     * @return le nom de la commande
     */
    protected abstract String getCommandName();

    /**
     * Exécute la commande définie par le test avec les arguments donnés.
     * @param args les arguments de la commande
     * @return le code de retour de la commande
     */
    protected int execute(String... args) {
        return executeCmd(getCommandName(), args);
    }

    /**
     * Exécute la commande spécifiée avec les arguments donnés.
     * @param command le nom de la commande
     * @param args les arguments
     * @return le code de retour
     */
    private int executeCmd(String command, String... args) {
        String[] cmdArgs = new String[args.length + 1];
        cmdArgs[0] = command;
        System.arraycopy(args, 0, cmdArgs, 1, args.length);
        return new CommandLine(new Site()).execute(cmdArgs);
    }

    /**
     * Crée un site de test basique.
     * @throws IOException si une erreur survient lors de la création du site
     */
    protected static void createBasicSite() throws IOException {
        Files.createDirectories(TEST_DIRECTORY);

        FilesHelper.copyDirectory(TEST_SRC_DIRECTORY, TEST_DIRECTORY);
    }

    /**
     * Supprime le site de test.
     * @throws IOException si une erreur survient lors de la suppression du site
     */
    protected static void deleteBasicSite() throws IOException {
        FilesHelper.cleanDirectory(TEST_DIRECTORY);
        Files.delete(TEST_DIRECTORY);
    }

    /**
     * Créé et construit le site de test.
     * @throws IOException si une erreur survient lors de la construction du site
     */
    protected static void buildBasicSite() throws IOException {
        createBasicSite();
        FilesHelper.copyDirectory(BUILD_SRC_DIRECTORY, BUILD_PATH);
    }

    /**
     * Supprime le dossier de build du site.
     * @throws IOException si une erreur survient lors de la suppression du dossier de build
     */
    protected static void cleanBuild() throws IOException {
        FilesHelper.cleanDirectory(BUILD_PATH);
        Files.delete(BUILD_PATH);
    }

    /**
     * Redirige les flux d'entrée/sortie.
     * @throws IOException si une erreur survient lors de la redirection
     */
    protected void redirectIO() throws IOException {
        this.in = System.in;
        this.out = System.out;
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        System.setIn(in);
        System.setOut(new PrintStream(out));
    }

    /**
     * Restaure les flux d'entrée/sortie.
     */
    protected void resetIO() {
        System.setIn(this.in);
        System.setOut(this.out);
    }

    /**
     * <a href="https://stackoverflow.com/a/57508242">Source</a>
     */
    protected static void awaitFile(Path target) throws IOException, InterruptedException {
        final Path name = target.getFileName();
        final Path targetDir = target.getParent();

        // If path already exists, return early
        if (Files.exists(target)) return;

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            final WatchKey watchKey = targetDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            // The file could have been created in the window between Files.readAttributes and Path.register
            if (Files.exists(target)) return;

            // The file is absent: watch events in parent directory
            WatchKey watchKey1 = null;
            boolean valid = true;
            do {
                long t0 = System.currentTimeMillis();
                watchKey1 = watchService.take();
                // Examine events associated with key
                for (WatchEvent<?> event : watchKey1.pollEvents()) {
                    Path path1 = (Path) event.context();
                    if (path1.getFileName().equals(name)) {
                        return;
                    }
                }
                valid = watchKey1.reset();
            } while (valid);
        }
    }
}
