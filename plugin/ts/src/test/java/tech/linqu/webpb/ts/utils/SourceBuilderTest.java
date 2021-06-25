package tech.linqu.webpb.ts.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SourceBuilderTest {

    @Test
    void shouldTestIsEmptySuccess() {
        SourceBuilder builder = new SourceBuilder();
        assertTrue(builder.isEmpty());
        builder.append("a");
        assertFalse(builder.isEmpty());
    }

    @Test
    void shouldTestTrimDuplicatedNewlineSuccess() {
        SourceBuilder builder = new SourceBuilder();
        builder.append("a");
        builder.trimDuplicatedNewline();
        assertEquals("a", builder.toString());

        builder.append("a");
        builder.trimDuplicatedNewline();
        assertEquals("aa", builder.toString());

        builder.append("\n");
        builder.trimDuplicatedNewline();
        assertEquals("aa\n", builder.toString());

        builder.append("\n");
        builder.trimDuplicatedNewline();
        assertEquals("aa\n", builder.toString());
    }

    @Test
    void shouldTestTrimLastSuccess() {
        SourceBuilder builder = new SourceBuilder();
        builder.append('\n');
        builder.trimLast('\n');
        assertTrue(builder.isEmpty());

        builder.append('\n');
        builder.trimLast('a');
        assertEquals("\n", builder.toString());
    }

    @Test
    void shouldTestContainsSuccess() {
        SourceBuilder builder = new SourceBuilder();
        assertFalse(builder.contains("hello"));
        builder.append("adfhelloa dk");
        assertTrue(builder.contains("hello"));
    }
}
