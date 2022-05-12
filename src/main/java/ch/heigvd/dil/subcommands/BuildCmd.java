package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.*;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.FilesHelper;
import ch.heigvd.dil.util.HTMLTemplater;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * @author Stéphane Marengo
 */
@Command(name = "build", description = "Builds the site")
public class BuildCmd implements Callable<Integer> {
    public static final String BUILD_DIR = "build";
    private static final String TEMPLATE_DIR = "templates";
    private static final String CONFIG_FILE = "config.yaml";

    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path path;

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
                            if (p.toString().endsWith(".md")) {
                                convertMdToHTML(templater, p, outPath);
                            } else {
                                Files.createDirectories(outPath.getParent());
                                Files.copy(p, outPath);
                            }
                        } catch (IOException e) {
                            System.err.println("An error occurred while copying the file: " + e.getMessage());
                        }
                    });
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

        Files.writeString(htmlPath, templater.inject(file));
    }
}
