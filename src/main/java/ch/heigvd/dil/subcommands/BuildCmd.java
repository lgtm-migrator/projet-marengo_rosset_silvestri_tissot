package ch.heigvd.dil.subcommands;


import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(name = "build", description = "Subcommand build !")
public class BuildCmd implements Callable<Integer> {
    @Override
    public Integer call() {
        System.out.println("Build command !");
        return 0;
    }
}
