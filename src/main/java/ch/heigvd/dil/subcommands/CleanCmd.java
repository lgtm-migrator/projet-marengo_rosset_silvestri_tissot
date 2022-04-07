package ch.heigvd.dil.subcommands;


import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(name = "clean", description = "Subcommand clean !")
public class CleanCmd implements Callable<Integer> {
    @Override
    public Integer call() {
        System.out.println("Clean command !");
        return 0;
    }
}
