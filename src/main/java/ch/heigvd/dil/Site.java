package ch.heigvd.dil;

import ch.heigvd.dil.subcommands.CleanCmd;
import ch.heigvd.dil.subcommands.BuildCmd;
import ch.heigvd.dil.subcommands.ServeCmd;
import ch.heigvd.dil.subcommands.NewCmd;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "site", subcommands = {
        CleanCmd.class,
        BuildCmd.class,
        ServeCmd.class,
        NewCmd.class
})
public class Site implements Callable<Integer> {
    public static void main(String[] args) {
        int rc = new CommandLine(new Site()).execute(args);
        System.exit(rc);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }
}