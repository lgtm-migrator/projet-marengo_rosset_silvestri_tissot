package ch.heigvd.dil.subcommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Tissot Olivier
 * @author Stéphane Marengo
 */
public class CleanCmdTest extends BaseCmdTest {
    private static final String NAME_SITE = "SiteClearCmdTest";
    private static final Path PATH = Paths.get(NAME_SITE);
    private static final String BUILD = "build";
    private static final String FILE = "file.txt";

    private static final String DIRECTORY = "dossierTest";

    protected String getCommandName() {
        return "clean";
    }

    @AfterAll
    static void clearConfig() throws IOException {
        if (!Files.exists(PATH)) return;

        try (Stream<Path> walk = Files.walk(PATH)) {
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    @BeforeEach
    public void createBuildFiles() throws IOException {
        clearConfig();

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
        execute(NAME_SITE);
        assertFalse(Files.exists(Paths.get(NAME_SITE, BUILD)));
    }
}
