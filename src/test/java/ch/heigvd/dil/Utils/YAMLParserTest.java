package ch.heigvd.dil.Utils;

import junit.framework.TestCase;
import org.junit.Test;

public class YAMLParserTest extends TestCase {
    @Test
    public void testParser() {
        YAMLParser parser = new YAMLParser();
        try {
            var data = parser.read("data.yml");
            assertEquals(data.get("author"), "Bob");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}