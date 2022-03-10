package ch.heigvd.dil;

import ch.heigvd.dil.subcommands.BuildCmd;
import ch.heigvd.dil.subcommands.ServeCmd;
import ch.heigvd.dil.subcommands.NewCmd;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "Main", subcommands = {
        BuildCmd.class
        ServeCmd.class,
        NewCmd.class
})
public class Main implements Callable<Integer> {
    public static void main(String[] args) {
        int rc = new CommandLine(new Main()).execute(args);
        System.exit(rc);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }
}