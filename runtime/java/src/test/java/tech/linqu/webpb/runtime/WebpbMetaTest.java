package tech.linqu.webpb.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class WebpbMetaTest {

    private final WebpbMeta webpbMeta = WebpbMeta.builder()
        .method("GET")
        .context("user")
        .path("/get/user")
        .tags("backend", "frontend")
        .build();

    @Test
    void shouldGetMethodSuccess() {
        assertEquals("GET", webpbMeta.getMethod());
    }

    @Test
    void shouldGetContextSuccess() {
        assertEquals("user", webpbMeta.getContext());
    }

    @Test
    void shouldGetPathSuccess() {
        assertEquals("/get/user", webpbMeta.getPath());
    }

    @Test
    void shouldGetTagsSuccess() {
        assertLinesMatch(Arrays.asList("backend", "frontend"), webpbMeta.getTags());
    }

    @Test
    void shouldToStringSuccess() {
        assertEquals(
            "WebpbMeta(method=GET, context=user, path=/get/user, tags=[backend, frontend])",
            webpbMeta.toString());
    }
}
