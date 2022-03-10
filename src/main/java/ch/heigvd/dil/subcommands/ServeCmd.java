package ch.heigvd.dil.subcommands;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "serve", description = "Subcommand serve !")
public class ServeCmd implements Callable<Integer> {
    @Override public Integer call() {
        System.out.println("Serve command !");
        return 0;
    }
}
