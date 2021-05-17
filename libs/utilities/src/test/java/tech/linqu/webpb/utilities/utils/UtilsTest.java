package tech.linqu.webpb.utilities.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void shouldNormalizeStringSuccess() {
        assertEquals("", Utils.normalize(""));
        assertEquals("", Utils.normalize("/"));
        assertEquals("", Utils.normalize("//"));
        assertEquals("/a", Utils.normalize("/a"));
        assertEquals("/a", Utils.normalize("a/"));
        assertEquals("/a", Utils.normalize("/a/"));
    }
}
