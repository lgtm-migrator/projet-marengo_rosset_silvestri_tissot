package ch.heigvd.dil.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Classe utilitaire en charge de construire (copier les fichiers et les convertir si nécessaire)
 * un site.
 *
 * @author Marengo Stéphane
 */
public class SiteBuilder {
    public static final String TEMPLATE_DIR = "templates";
    static final String CONFIG_FILE = "config.yaml";

    private final Path srcPath;
    private final Path outPath;
    private final HTMLTemplater templater;

    /**
     * Construit un SiteBuilder pour le chemin spécifié.
     * @param src le chemin contenant les sources
     * @param buildDir le nom du dossier de build
     * @throws IOException si le dossier de build ne peut pas être créé ou si le templater ne peut pas être créé
     */
    public SiteBuilder(Path src, String buildDir) throws IOException {
        srcPath = src;
        outPath = srcPath.resolve(buildDir);

        try {
            FilesHelper.cleanDirectory(outPath);
        } catch (IOException e) {
            throw new IOException("An error occurred while creating the directory: " + e.getMessage());
        }

        try {
            templater = new HTMLTemplater(srcPath.resolve(CONFIG_FILE), srcPath.resolve(TEMPLATE_DIR));
        } catch (IOException e) {
            throw new IOException("An error occurred while creating the templater: " + e.getMessage());
        }
    }

    /**
     * Construit le site et affiche des messages d'erreur en cas de problème.
     * @return vrai si tout s'est bien passé, faux sinon
     */
    public boolean build() {
        try {
            buildSite();
        } catch (IOException e) {
            System.err.println("An error occurred while building the site: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Construit le site.
     * @throws IOException si une erreur IO survient
     */
    private void buildSite() throws IOException {
        try (Stream<Path> walk = Files.walk(srcPath)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> !p.toString().endsWith(".yml"))
                    .filter(p -> !p.toString().endsWith(".yaml"))
                    .forEach(this::buildFile);
        }
    }

    /**
     * Copie le fichier spécifié et le convertit en HTML si nécessaire.
     * Affiche un message d'erreur en cas d'erreur.
     * @param file le fichier à copier
     * @return vrai si le fichier a pu être copié, faux sinon
     */
    public boolean buildFile(Path file) {
        var outPath = getOutputpath(file);

        try {
            if (file.toString().endsWith(".md")) {
                convertMdToHTML(templater, file, outPath);
            } else {
                Files.createDirectories(outPath.getParent());
                Files.copy(file, outPath);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while copying the file: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Converti le fichier markdown donné en HTML dans le dossier de build.
     *
     * @param file le fichier markdown à convertir
     * @param dest le fichier de destination
     * @throws IOException si une erreur IO survient
     */
    private void convertMdToHTML(HTMLTemplater templater, Path file, Path dest) throws IOException {
        var htmlPath = dest.resolveSibling(dest.getFileName().toString().replace(".md", ".html"));

        Files.createDirectories(htmlPath.getParent());
        Files.writeString(htmlPath, templater.inject(file));
    }

    /**
     * Obtient le chemin de destination du fichier donné.
     * @param file le fichier
     * @return le chemin de destination
     */
    public Path getOutputpath(Path file) {
        return outPath.resolve(srcPath.relativize(file));
    }

    /**
     * Retourne le chemin du dossier de build.
     * @return le chemin du dossier de build
     */
    public Path getBuildPath() {
        return outPath;
    }

    /**
     * Retourne le chemin du dossier de template.
     * @return le chemin du dossier de template
     */
    public Path getTemplatePath() {
        return srcPath.resolve(TEMPLATE_DIR);
    }
}
