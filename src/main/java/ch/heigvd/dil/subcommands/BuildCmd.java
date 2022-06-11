package ch.heigvd.dil.subcommands;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ExitCode;

/**
 * @author Stéphane Marengo
 */
@Command(name = "build", description = "Builds the site")
public class BuildCmd extends BuildableCmd {
    @Override
    public Integer call() {
        if (!build()) return ExitCode.SOFTWARE;

        if (withWatcher()) {
            if (!startWatching()) return ExitCode.SOFTWARE;
            waitForExit();

            if (!stopWatching()) return ExitCode.SOFTWARE;
        }

        return ExitCode.OK;
    }
}
