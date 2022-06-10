package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.ExitCode;
import static picocli.CommandLine.Parameters;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import ch.heigvd.dil.util.FilesHelper;
import ch.heigvd.dil.util.SiteBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

/**
 * @author Tissot Olivier
 * @author Stéphane Marengo
 */
@Command(name = "clean", description = "Delete the build directory")
public class CleanCmd implements Callable<Integer> {
    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path path;

    @Override
    public Integer call() {
        Path buildDir = path.resolve(SiteBuilder.BUILD_DIR);

        if (!Files.isDirectory(buildDir)) return ExitCode.OK;

        try {
            FilesHelper.cleanDirectory(buildDir);
            Files.delete(buildDir);
        } catch (IOException e) {
            System.err.println("Error while cleaning the build directory: " + e.getMessage());
            return ExitCode.SOFTWARE;
        }

        return ExitCode.OK;
    }
}
