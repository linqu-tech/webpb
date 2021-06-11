package tech.linqu.webpb.dump;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.tests.Dumps;

class MainTest {

    @Test
    void shouldConstructSuccess() {
        Main main = new Main();
        assertNotNull(main);
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
