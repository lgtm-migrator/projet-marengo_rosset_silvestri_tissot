package ch.heigvd.dil.subcommands;


import ch.heigvd.dil.Site;
import org.junit.jupiter.api.BeforeEach;
import picocli.CommandLine;

/**
 * Classe de base permettant de tester les sous-commandes.
 *
 * @author Marengo Stéphane
 * @author Silvestri Géraud
 */
abstract class BaseCmdTest {
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
    protected void setUp() {
        cmd = new CommandLine(new Site());
    }

    /**
     * Retourne le code de retour suite à l'exécution de la commande.
     * @return le code de retour
     */
    protected int getReturnCode() {
        return returnCode;
    }
}
