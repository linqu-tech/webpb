package tech.linqu.webpb.dump;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void shouldConstructSuccess() {
        assertDoesNotThrow(Main::new);
    }

    @Test
    void shouldCallMainSuccess() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        InputStream inputStream = getClass().getResourceAsStream("test.dump");
        System.setIn(inputStream);
        Main.main(null);
        assertTrue(outputStream.toByteArray().length > 0);
    }
}
