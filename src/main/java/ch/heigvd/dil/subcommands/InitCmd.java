package ch.heigvd.dil.subcommands;


import ch.heigvd.dil.util.HTMLConverter;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "init", description = "Subcommand init !")
public class InitCmd implements Callable<Integer> {

    @CommandLine.Parameters(arity = "1", description = "Pas de chemins specifie.") String path;
    @Override
    public Integer call() {
        System.out.println("Init command !");

        try{
            createFolder(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Impossibilite de creer le dossier fournit.");
            return -1;
        }

        Path filePath = Paths.get(path, "index.html");
        String copyFilename = "index.default.md";

        try {
            deleteFile(path, copyFilename);
            Files.copy(getResourceAsStream(copyFilename), Paths.get(path , copyFilename));
            Files.write(filePath, HTMLConverter.convertMarkdownFiles(path + "/" + copyFilename).getBytes(StandardCharsets.UTF_8));
            deleteFile(path, copyFilename);
        } catch (IOException e) {
            try{
                deleteFile(path, "index.html");
            } catch (IOException ex) {
                System.out.println("Impossibilite de supprimer le fichier d'index. Celui-ci est errone.");
            }
            System.out.println("Impossibilite de mettre a jour le fichier d'index.");
            return -1;
        }

        return 0;
    }

    /**
     * Retourne un InputStream sur le fichier de resource passé en paramètre.
     *
     * @param fileName le nom du fichier de resource
     * @return un InputStream sur le fichier
     * @throws IOException si le fichier ne peut pas être ouvert
     */
    private static InputStream getResourceAsStream(String fileName) throws IOException {
        InputStream in = InitCmd.class.getClassLoader().getResourceAsStream(fileName);
        if (in == null)
            throw new IOException("File " + fileName + " not found in resources.");

        return in;
    }

    /**
     * Création d'un fichier
     * @param path Chemin du fichier
     * @param fileName Nom du fichier
     * @throws IOException Si sa création ne fonctionne pas
     */
    private Path createFile(String path, String fileName) throws IOException {
        Path p = Paths.get(path, fileName);

        if (!Files.exists(p)) {
            Files.createFile(p);
        }
        return p;
    }

    /**
     * Création d'un dossier
     * @param path Chemin du dossier
     * @throws IOException Si sa création ne fonctionne pas
     */
    private void createFolder(String path) throws IOException {
        Path p = Paths.get(path);
        Files.createDirectories(p);
    }

    /**
     * Suppression d'un fichier s'il existe
     * @param path Chemin du fichier
     * @param fileName Nom du fichier
     * @throws IOException Si sa suppression ne fonctionne pas
     */
    private void deleteFile(String path, String fileName) throws IOException {
        Path p = Paths.get(path, fileName);
        Files.deleteIfExists(p);
    }
}
