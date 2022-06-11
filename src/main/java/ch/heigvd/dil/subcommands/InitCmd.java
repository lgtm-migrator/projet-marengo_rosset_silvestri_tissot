package ch.heigvd.dil.subcommands;


import ch.heigvd.dil.converter.PathConverter;
import ch.heigvd.dil.util.SiteBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

/**
 * @author Loïc Rosset
 * @author Stéphane Marengo
 */
@Command(name = "init", description = "Initialize a new site by creating a config file and an index file")
public class InitCmd implements Callable<Integer> {

    private static final String CONFIG_FILE = "config.default.yaml";
    private static final String INDEX_FILE = "index.default.md";
    private static final String LAYOUT_FILE = "layout.default.html";
    private static final String MENU_FILE = "menu.default.html";

    @Parameters(description = "Path to the new site", converter = PathConverter.class)
    private Path path;

    @Override
    public Integer call() {
        try {
            Files.createDirectories(path);
            Files.createDirectories(path.resolve(SiteBuilder.TEMPLATE_DIR));
        } catch (IOException e) {
            System.err.println("Error while creating the site: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        try {
            copyFileIfMissing(CONFIG_FILE, path.resolve("config.yaml"));
            copyFileIfMissing(INDEX_FILE, path.resolve("index.md"));
            copyFileIfMissing(LAYOUT_FILE, path.resolve(SiteBuilder.TEMPLATE_DIR + "/layout.html"));
            copyFileIfMissing(MENU_FILE, path.resolve(SiteBuilder.TEMPLATE_DIR + "/menu.html"));
        } catch (IOException e) {
            System.err.println("Error while creating the default files: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        System.out.println("Site successfully created at " + path.toAbsolutePath());

        return ExitCode.OK;
    }

    /**
     * Retourne un InputStream sur le fichier de resource passé en paramètre.
     *
     * @param fileName le nom du fichier de resource
     * @return un InputStream sur le fichier
     * @throws IOException si le fichier ne peut pas être ouvert
     */
    private InputStream getResourceAsStream(String fileName) throws IOException {
        InputStream in = InitCmd.class.getClassLoader().getResourceAsStream(fileName);
        if (in == null) throw new IOException("File " + fileName + " not found in resources.");

        return in;
    }

    /**
     * Copie le fichier de resource à la destination
     *
     * @param srcFile  le nom du fichier de resource à copier
     * @param destFile la destination du fichier
     * @throws IOException si une erreur survient lors de la copie
     */
    private void copyFileIfMissing(String srcFile, Path destFile) throws IOException {
        if (Files.isRegularFile(destFile)) return;

        try (InputStream in = getResourceAsStream(srcFile)) {
            Files.copy(in, destFile);
        }
    }
}
