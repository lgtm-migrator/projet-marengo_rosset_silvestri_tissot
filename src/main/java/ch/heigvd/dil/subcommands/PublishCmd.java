package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.ExitCode;
import static picocli.CommandLine.Parameters;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

/**
 * @author Stéphane Marengo
 */
@Command(name = "publish", description = "Publish the site to a remote directory")
public class PublishCmd implements Callable<Integer> {
    @Parameters(description = "Path to the remote directory", converter = PathDirectoryConverter.class)
    private Path path;

    @Override
    public Integer call() {
        Path buildPath = path.resolve(BuildCmd.BUILD_DIR);
        if (!Files.isDirectory(buildPath)) {
            System.err.println("The build directory does not exist. You should run the build command first.");
            return ExitCode.USAGE;
        }

        return ExitCode.OK;
    }
}
