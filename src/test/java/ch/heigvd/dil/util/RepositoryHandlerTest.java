package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class RepositoryHandlerTest {
    @Test
    void itShouldThrowOnInvalidPath() throws IOException {
        RepositoryHandler repoHandler = new RepositoryHandler("/src/test-repo/.git");
        repoHandler.createRepository();
    }
}
