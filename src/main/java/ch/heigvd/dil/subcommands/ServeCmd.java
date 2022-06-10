package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.ExitCode;
import static picocli.CommandLine.Parameters;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.SiteBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Callable;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * @author Stéphane Marengo
 */
@Command(name = "serve", description = "Start a web server to preview the site")
public class ServeCmd implements Callable<Integer> {
    static final String STOP_KEYWORD = "exit";
    private static final int DEFAULT_PORT = 8080;

    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path path;

    @Option(
            names = {"-p", "--port"},
            description = "Port to use for the web server",
            defaultValue = "" + DEFAULT_PORT)
    private int port;

    private Server server;

    @Override
    public Integer call() {
        Path buildPath = path.resolve(SiteBuilder.BUILD_DIR);
        if (port < 0 || port > 65535) {
            System.err.println("Invalid port number");
            return ExitCode.USAGE;
        }
        if (!Files.isDirectory(buildPath)) {
            System.err.println("The build directory does not exist. You should run the build command first.");
            return ExitCode.USAGE;
        }

        System.out.println("Starting server...");

        if (!startServer(buildPath)) return ExitCode.SOFTWARE;

        System.out.println("Type '" + STOP_KEYWORD + "' to stop the server.");
        waitForExit();

        if (!stopServer()) return ExitCode.SOFTWARE;

        return ExitCode.OK;
    }

    /**
     * Lance le serveur sur le port par défaut ou sur un port libre.
     * @param srcPath le chemin vers le dossier contenant les sources
     * @return vrai si le serveur a pu être lancé, faux sinon
     */
    private boolean startServer(Path srcPath) {
        try {
            startServer(port, srcPath);
        } catch (Exception e) {
            System.out.println(
                    "Could not start the server on port " + port + ". Trying to start it on another port...");
            try {
                startServer(0, srcPath);
            } catch (Exception e2) {
                System.err.println("Could not start the server: " + e2.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Lance le serveur sur le port spécifié ou sur un port libre.
     * @param port le port à utiliser en priorité
     * @param srcPath le chemin vers le dossier contenant les sources
     * @throws Exception si le serveur n'a pas pu être lancé
     */
    private void startServer(int port, Path srcPath) throws Exception {
        server = new Server(port);
        ResourceHandler handler = new ResourceHandler();
        handler.setBaseResource(Resource.newResource(srcPath));
        handler.setDirectoriesListed(false);
        server.setHandler(handler);
        server.start();
        System.out.println("Server started at " + server.getURI());
    }

    /**
     * Arrête le serveur.
     * @return vrai si le serveur a pu être arrêté, faux sinon
     */
    private boolean stopServer() {
        try {
            System.out.println("Stopping server...");
            server.stop();
            System.out.println("Server stopped.");
        } catch (Exception e) {
            System.err.println("Could not stop the server: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Boucle jusqu'à ce que l'utilisateur entre STOP_KEYWORD.
     */
    private void waitForExit() {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNext(STOP_KEYWORD)) {
            scanner.nextLine();
        }
    }
}
