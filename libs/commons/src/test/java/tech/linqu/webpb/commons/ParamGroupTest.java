package tech.linqu.webpb.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParamGroupTest {

    @Test
    void shouldCreateSuccessWhenPathIsNull() {
        // given
        String path = null;

        // when
        ParamGroup group = ParamGroup.of(path);

        // then
        assertTrue(group.getParams().isEmpty());
        assertEquals("", group.getSuffix());
    }

    @Test
    void shouldCreateSuccessWhenPathIsEmpty() {
        // given
        String path = "";

        // when
        ParamGroup group = ParamGroup.of(path);

        // then
        assertTrue(group.getParams().isEmpty());
        assertEquals("", group.getSuffix());
    }

    @Test
    void shouldCreateSuccessWhenPathIsRoot() {
        // given
        String path = "/";

        // when
        ParamGroup group = ParamGroup.of(path);

        // then
        assertTrue(group.getParams().isEmpty());
        assertEquals("/", group.getSuffix());
    }

    @Test
    void shouldCreateSuccessWhenPathWithParams() {
        // given
        String path = "/{a}/b{c}/{d}e/f{g.h}i/j";

        // when
        ParamGroup group = ParamGroup.of(path);

        // then
        assertEquals(4, group.getParams().size());
        assertEquals("i/j", group.getSuffix());
        // 1
        assertEquals("/", group.getParams().get(0).getPrefix());
        assertNull(group.getParams().get(0).getKey());
        assertEquals("a", group.getParams().get(0).getAccessor());
        // 2
        assertEquals("/b", group.getParams().get(1).getPrefix());
        assertNull(group.getParams().get(1).getKey());
        assertEquals("c", group.getParams().get(1).getAccessor());
        // 3
        assertEquals("/", group.getParams().get(2).getPrefix());
        assertNull(group.getParams().get(2).getKey());
        assertEquals("d", group.getParams().get(2).getAccessor());
        // 4
        assertEquals("e/f", group.getParams().get(3).getPrefix());
        assertNull(group.getParams().get(3).getKey());
        assertEquals("g.h", group.getParams().get(3).getAccessor());
    }

    @Test
    void shouldCreateSuccessWhenPathWithQueries() {
        // given
        String path = "a={b}&c={d.e}";

        // when
        ParamGroup group = ParamGroup.of(path);

        // then
        assertEquals(2, group.getParams().size());
        assertEquals("", group.getSuffix());
        // 1
        assertEquals("", group.getParams().get(0).getPrefix());
        assertEquals("a", group.getParams().get(0).getKey());
        assertEquals("b", group.getParams().get(0).getAccessor());
        // 2
        assertEquals("", group.getParams().get(1).getPrefix());
        assertEquals("c", group.getParams().get(1).getKey());
        assertEquals("d.e", group.getParams().get(1).getAccessor());
    }

    @Test
    void shouldCreateSuccessWhenPathWithParamsAndQueries() {
        // given
        String path = "a/{b}/c?e={f}";

        // when
        ParamGroup group = ParamGroup.of(path);

        // then
        assertEquals(2, group.getParams().size());
        assertEquals("", group.getSuffix());
        // 1
        assertEquals("a/", group.getParams().get(0).getPrefix());
        assertNull(group.getParams().get(0).getKey());
        assertEquals("b", group.getParams().get(0).getAccessor());
        // 2
        assertEquals("/c?", group.getParams().get(1).getPrefix());
        assertEquals("e", group.getParams().get(1).getKey());
        assertEquals("f", group.getParams().get(1).getAccessor());
    }
}
