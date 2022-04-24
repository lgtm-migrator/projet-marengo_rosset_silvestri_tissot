package ch.heigvd.dil.subcommands;


import ch.heigvd.dil.Site;
import ch.heigvd.dil.util.FilesHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
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
    protected static final Path BUILD_PATH = TEST_DIRECTORY.resolve(BuildCmd.BUILD_DIR);

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

        copyDirectory(TEST_SRC_DIRECTORY, TEST_DIRECTORY);
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
        copyDirectory(BUILD_SRC_DIRECTORY, BUILD_PATH);
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
     * Copie un répertoire.
     * @param src le répertoire source
     * @param dst le répertoire de destination
     * @throws IOException si une erreur survient lors de la copie
     */
    protected static void copyDirectory(Path src, Path dst) throws IOException {
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
