package ch.heigvd.dil.Utils;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Map;

public class YAMLParserTest extends TestCase {
    @Test
    public void testParser() {
        Map<String, Object> data = null;
        YAMLParser parser = new YAMLParser();
        try {
            data = parser.read("data.yml");
            assertSame(data.get("author"), "Bob");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}