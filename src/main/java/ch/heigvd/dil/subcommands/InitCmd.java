package ch.heigvd.dil.subcommands;

import ch.heigvd.dil.util.HTMLConverter;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Command;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * @author Loïc Rosset
 */
@Command(name = "init", description = "Subcommand init !")
public class InitCmd implements Callable<Integer> {

    @Parameters(arity = "1", description = "Root path for the init site.")

    private Path path;

    @Override
    public Integer call() {
        System.out.println("Init command !");

        try{
            createFolder(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The folders provided as parameters  cannot be created.");
            return ExitCode.USAGE;
        }

        Path filePath = path.resolve("index.html");
        Path copyPath = path.resolve("index.default.md");

        try {
            deleteFile(copyPath);
            Files.copy(getResourceAsStream("index.default.md"), copyPath);
            Files.writeString(filePath, HTMLConverter.fromMarkdown(Files.readString(copyPath)));
            deleteFile(copyPath);
        } catch (IOException e) {
            try{
                deleteFile(filePath);
            } catch (IOException ex) {
                System.out.println("The system had a problem during the creation of the index and cannot delete it.");
            }
            System.out.println("The index file cannot be created or updated.");
            return ExitCode.USAGE;
        }

        return ExitCode.OK;
    }

    /**
     * Retourne un InputStream sur le fichier de resource passé en paramètre.
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
     * Création d'un dossier
     * @param path Chemin du dossier
     * @throws IOException Si sa création ne fonctionne pas
     */
    private void createFolder(Path path) throws IOException {
        Files.createDirectories(path);
    }

    /**
     * Suppression d'un fichier s'il existe
     * @param path Chemin du fichier
     * @throws IOException Si sa suppression ne fonctionne pas
     */
    private void deleteFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}
