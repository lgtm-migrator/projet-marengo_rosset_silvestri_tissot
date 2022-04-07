package ch.heigvd.dil.subcommands;


import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(name = "new", description = "Subcommand new !")
public class NewCmd implements Callable<Integer> {
    @Override
    public Integer call() {
        System.out.println("New command!");
        return 0;
    }
}
