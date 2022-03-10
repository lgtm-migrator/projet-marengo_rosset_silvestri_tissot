package ch.heigvd.dil;

import ch.heigvd.dil.subcommands.ServeCmd;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "Main", subcommands = {
        ServeCmd.class
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