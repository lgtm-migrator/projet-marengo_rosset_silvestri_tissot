package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.FilesHelper;
import ch.heigvd.dil.util.HTMLConverter;
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

    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path path;

    @Override
    public Integer call() {
        Path out = path.resolve(BUILD_DIR);
        try {
            FilesHelper.cleanDirectory(out);
        } catch (IOException e) {
            System.err.println("An error occurred while creating the directory: " + e.getMessage());
            return 1;
        }

        try {
            build(path, out);
        } catch (IOException e) {
            System.err.println("An error occurred while building the site: " + e.getMessage());
            return 1;
        }

        System.out.println("Site built successfully to " + out.toAbsolutePath());

        return 0;
    }

    /**
     * Construit le site.
     * @param srcDir le dossier contenant les sources
     * @param destDir le dossier de destination
     * @throws IOException si une erreur IO survient
     */
    private void build(Path srcDir, Path destDir) throws IOException {
        try (Stream<Path> walk = Files.walk(srcDir)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> !p.toString().endsWith(".yml"))
                    .filter(p -> !p.toString().endsWith(".yaml"))
                    .forEach(p -> {
                        var outPath = destDir.resolve(p.subpath(1, p.getNameCount()));
                        try {
                            if (p.toString().endsWith(".md")) {
                                convertMdToHTML(p, outPath);
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
    private void convertMdToHTML(Path file, Path dest) throws IOException {
        var htmlPath = dest.resolveSibling(dest.getFileName().toString().replace(".md", ".html"));

        String content = Files.readString(file);
        String converted = HTMLConverter.fromMarkdown(content);
        Files.writeString(htmlPath, converted);
    }
}
