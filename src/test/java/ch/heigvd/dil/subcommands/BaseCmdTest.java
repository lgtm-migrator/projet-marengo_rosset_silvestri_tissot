package ch.heigvd.dil.subcommands;


import ch.heigvd.dil.Site;
import ch.heigvd.dil.util.FilesHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import picocli.CommandLine;

/**
 * Classe de base permettant de tester les sous-commandes.
 *
 * @author Marengo Stéphane
 * @author Silvestri Géraud
 */
abstract class BaseCmdTest {
    protected static final Path TEST_SRC_DIRECTORY = Path.of("src", "test", "resources", "basicSite");
    protected static final Path TEST_DIRECTORY = Path.of("basicSite");
    private CommandLine cmd;
    private int returnCode;

    /**
     * Retourne le nom de la commande à tester.
     * @return le nom de la commande
     */
    protected abstract String getCommandName();

    /**
     * Exécute la commande avec les arguments donnés.
     * @param args les arguments de la commande
     * @return le code de retour de la commande
     */
    protected int execute(String... args) {
        String[] cmdArgs = new String[args.length + 1];
        cmdArgs[0] = getCommandName();
        System.arraycopy(args, 0, cmdArgs, 1, args.length);
        return returnCode = cmd.execute(cmdArgs);
    }

    /**
     * Initialise l'application entre chaque test.
     */
    @BeforeEach
    protected void setUpCmd() {
        cmd = new CommandLine(new Site());
    }

    /**
     * Retourne le code de retour suite à l'exécution de la commande.
     * @return le code de retour
     */
    protected int getReturnCode() {
        return returnCode;
    }

    /**
     * Crée un site de test basique.
     * @throws IOException si une erreur survient lors de la création du site
     */
    @BeforeAll
    protected static void createBasicSite() throws IOException {
        Files.createDirectories(TEST_DIRECTORY);

        copyDirectory(TEST_SRC_DIRECTORY, TEST_DIRECTORY);
    }

    /**
     * Supprime le site de test.
     * @throws IOException si une erreur survient lors de la suppression du site
     */
    @AfterAll
    protected static void deleteBasicSite() throws IOException {
        FilesHelper.cleanDirectory(TEST_DIRECTORY);
        Files.delete(TEST_DIRECTORY);
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
