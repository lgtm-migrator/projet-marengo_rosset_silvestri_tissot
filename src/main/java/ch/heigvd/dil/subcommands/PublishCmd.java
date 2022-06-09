package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.ExitCode;
import static picocli.CommandLine.Parameters;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.RepositoryHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author Stéphane Marengo
 */
@Command(name = "publish", description = "Publish the site to a remote directory")
public class PublishCmd implements Callable<Integer> {
    public static final String GIT_FILE = ".git";

    @Parameters(description = "Path to the remote directory", converter = PathDirectoryConverter.class)
    private Path path;

    @Override
    public Integer call() {
        // Copie dossier build dans repo tmp git puis push puis delete du dossier git

        Path out = path.resolve(GIT_FILE);

        if (!Files.exists(out)) {
            throw new CommandLine.TypeConversionException("Not a file.");
        }

        RepositoryHandler repoHandler = new RepositoryHandler(out.toString());

        try {
            repoHandler.createRepository();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            repoHandler.transfertFile();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return ExitCode.OK;
    }
}
