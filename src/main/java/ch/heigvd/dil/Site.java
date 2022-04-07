package ch.heigvd.dil;


import ch.heigvd.dil.subcommands.BuildCmd;
import ch.heigvd.dil.subcommands.CleanCmd;
import ch.heigvd.dil.subcommands.NewCmd;
import ch.heigvd.dil.subcommands.ServeCmd;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;

@Command(
        name = "site",
        subcommands = {CleanCmd.class, BuildCmd.class, ServeCmd.class, NewCmd.class},
        mixinStandardHelpOptions = true,
        versionProvider = Site.VersionProvider.class)
public class Site implements Callable<Integer> {
    private static final String CONFIG_FILE = "project.properties";

    public static void main(String[] args) {
        int rc = new CommandLine(new Site()).execute(args);
        System.exit(rc);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    static class VersionProvider implements IVersionProvider {
        public String[] getVersion() {
            Properties prop = new Properties();
            try {
                InputStream t = VersionProvider.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
                prop.load(t);

                return new String[] {"Version du logiciel: " + prop.getProperty("version")};
            } catch (IOException e) {
                return new String[] {"Unable to read from " + CONFIG_FILE + ": " + e};
            }
        }
    }
}
