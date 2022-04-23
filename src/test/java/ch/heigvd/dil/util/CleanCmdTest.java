package ch.heigvd.dil.util;

import ch.heigvd.dil.Site;
import ch.heigvd.dil.subcommands.CleanCmd;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Tissot Olivier
 * @author Stéphane Marengo
 */

public class CleanCmdTest {
    private static final String NAME_SITE = "SiteClearCmdTest";
    private static final Path PATH = Paths.get(NAME_SITE);
    private static final String BUILD = "build";
    private static final String FILE = "file.txt";

    private static final String DIRECTORY = "dossierTest";

    /*@After
    public void clearConfig() throws IOException {
        if (!Files.exists(PATH)) return;

        try (Stream<Path> walk = Files.walk(PATH)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }*/

    @Before
    public void createBuildFiles() throws IOException {
        //clearConfig();

        if (!Files.exists(PATH)) {
            Files.createDirectory(PATH);
            Files.createDirectory(Paths.get(NAME_SITE, BUILD));
            Files.createFile(Paths.get(NAME_SITE, BUILD, FILE));
            Files.createDirectory(Paths.get(NAME_SITE, BUILD, DIRECTORY));
            Files.createFile(Paths.get(NAME_SITE, BUILD, DIRECTORY, FILE));
        }
    }

    @Test
    public void itShouldDeleteTheBuildDirAndItsContent() {
        var s = new CleanCmd();
        var cmd = new CommandLine(s).execute("clean", "SiteClearCmdTest");
        assertFalse(Files.exists(Paths.get(NAME_SITE, BUILD)));
    }

}
