package ch.heigvd.dil.subcommands;


import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(name = "serve", description = "Subcommand serve !")
public class ServeCmd implements Callable<Integer> {
    @Override
    public Integer call() {
        System.out.println("Serve command !");
        return 0;
    }
}
