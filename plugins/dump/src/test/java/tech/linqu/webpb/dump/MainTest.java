package tech.linqu.webpb.dump;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.tests.Dumps;

class MainTest {

    @Test
    void shouldConstructSuccess() {
        assertDoesNotThrow(Main::new);
    }

    @Test
    void shouldCallMainSuccess() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Dumps.TEST1.pipe();
        Main.main(null);
        assertTrue(outputStream.toByteArray().length > 0);
    }
}
