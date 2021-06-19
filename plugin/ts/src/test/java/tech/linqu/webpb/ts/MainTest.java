package tech.linqu.webpb.ts;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import tech.linqu.webpb.tests.Dumps;

class MainTest {

    @Test
    void shouldConstructSuccess() {
        assertDoesNotThrow(Main::new);
    }

    @Test
    void shouldGenerateSuccess() {
        Dumps.TEST1.pipe();
        assertDoesNotThrow(() -> Main.main(null));
    }
}
