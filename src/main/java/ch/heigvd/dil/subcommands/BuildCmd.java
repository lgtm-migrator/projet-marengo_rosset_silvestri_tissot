package ch.heigvd.dil.subcommands;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "build", description = "Subcommand build !")
public class BuildCmd implements Callable<Integer> {
    @Override public Integer call() {
        System.out.println("Build command !");
        return 0;
    }
}