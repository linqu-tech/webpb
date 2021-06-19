package tech.linqu.webpb.utilities.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.linqu.webpb.utilities.test.TestUtils.compareToFile;
import static tech.linqu.webpb.utilities.test.TestUtils.createRequest;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.linqu.webpb.tests.Dumps;

class TestUtilsTest {

    @Test
    void shouldCreateRequestContextSuccess() {
        assertDoesNotThrow(() -> createRequest(Dumps.TEST1));
    }

    @Test
    void shouldCreateRequestThrowExceptionWhenInputError() {
        System.setIn(new ByteArrayInputStream("abc".getBytes()));
        Dumps dumps = Mockito.mock(Dumps.class);
        assertThrows(RuntimeException.class, () -> createRequest(dumps));
    }

    @Test
    void shouldCompareToFileSuccess() {
        assertTrue(compareToFile("abcd\"ef\"g'hi'", "/compare.txt", false));
        assertFalse(compareToFile("abcd\"ef\"g'hi'", "/compare.txt", true));
        assertThrows(RuntimeException.class, () -> compareToFile("", "/any", true));
    }
}
