package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Parameters;

import ch.heigvd.dil.converter.PathDirectoryConverter;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * @author Stéphane Marengo
 */
@Command(name = "build", description = "Builds the site")
public class BuildCmd implements Callable<Integer> {
    private static final String BUILD_DIR = "build";

    @Parameters(description = "Path to the sources directory", converter = PathDirectoryConverter.class)
    private Path path;

    @Override
    public Integer call() {
        System.out.println("Build command !");
        return 0;
    }
}
