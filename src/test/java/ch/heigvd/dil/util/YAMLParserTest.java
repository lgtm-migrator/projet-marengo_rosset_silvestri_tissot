package ch.heigvd.dil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

public class YAMLParserTest {
    final String PATH = "src/test/resources/data.yml";
    final String WRONG_FILE = "src/test/resources/data.txt";
    final YAMLParser parser = new YAMLParser();

    @Test
    public void itShouldParseCorrectly() throws Exception {
        var data = parser.parseFromFile(PATH);
        assertEquals(data.get("author"), "Bob");
        assertEquals(data.get("version"), "0.0.1");
        assertEquals(data.get("date"), "today");
    }

    @Test
    public void itShouldThrowAFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> parser.parseFromFile("resources/notAFile.txt"));
    }

    @Test
    public void itShouldThrowOnNonValidFile() {
        assertThrows(ClassCastException.class, () -> parser.parseFromFile(WRONG_FILE));
    }

    @Test
    public void itShouldParseCorrectlyAListOfLine() throws FileNotFoundException {
        Scanner s = new Scanner(new File(WRONG_FILE));
        ArrayList<String> list = new ArrayList<>();
        while (s.hasNext()) {
            list.add(s.next());
        }
        s.close();
        var data = parser.parseFromString(list);
        assertEquals(data.get("author"), "Bob");
        assertEquals(data.get("version"), "0.0.1");
        assertEquals(data.get("date"), "today");
    }

    @Test
    public void itShouldBeNullOnNonValidData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("aaaaa");
        var data = parser.parseFromString(list);
        assertEquals(0, data.size());
    }
}
