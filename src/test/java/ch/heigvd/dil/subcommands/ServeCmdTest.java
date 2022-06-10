package ch.heigvd.dil.subcommands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static picocli.CommandLine.ExitCode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Marengo Stéphane
 */
class ServeCmdTest extends BaseCmdTest {
    private static final String NOT_A_DIRECTORY = "notADirectory/";

    @Override
    protected String getCommandName() {
        return "serve";
    }

    @BeforeEach
    protected void setUp() throws IOException {
        buildBasicSite();
    }

    @AfterEach
    protected void clean() throws IOException {
        deleteBasicSite();
    }

    @Test
    void itShouldThrowOnInvalidPath() {
        assertEquals(ExitCode.USAGE, execute(INVALID_PATH));
    }

    @Test
    void itShouldThrowOnNotADirectory() {
        assertEquals(ExitCode.USAGE, execute(NOT_A_DIRECTORY));
    }

    @Test
    void itShouldThrowOnMissingPath() {
        assertEquals(ExitCode.USAGE, execute());
    }

    @Test
    void itShouldThrowOnMissingBuild() throws IOException {
        cleanBuild();
        assertFalse(Files.isDirectory(BUILD_PATH));
        assertEquals(ExitCode.USAGE, execute(TEST_DIRECTORY.toString()));
    }

    @Test
    void itShouldServeHttpRequests() throws Exception {
        int port = 8080;
        URI url = URI.create("http://localhost:" + port + "/index.html");
        redirectIO();

        var future = CompletableFuture.supplyAsync(() -> {
            return execute(TEST_DIRECTORY.toString(), "-p", "" + port);
        });

        var client = HttpClient.newHttpClient();
        var request =
                HttpRequest.newBuilder(url).header("accept", "application/json").build();

        while (true) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                break;
            }
        }
        System.out.println("exit");
        assertEquals(ExitCode.OK, future.join());
        resetIO();
    }
}
