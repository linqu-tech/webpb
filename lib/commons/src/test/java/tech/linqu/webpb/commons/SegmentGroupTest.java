/*
 * Copyright (c) 2020 linqu.tech, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.linqu.webpb.commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SegmentGroupTest {

    @Test
    void shouldCreateSuccessWhenPathIsNull() {
        // given
        String path = null;

        // when
        SegmentGroup group = SegmentGroup.of(path);

        // then
        assertTrue(group.isEmpty());
        assertEquals("", group.getSuffix());
    }

    @Test
    void shouldCreateSuccessWhenPathIsEmpty() {
        // given
        String path = "";

        // when
        SegmentGroup group = SegmentGroup.of(path);

        // then
        assertTrue(group.isEmpty());
        assertEquals("", group.getSuffix());
    }

    @Test
    void shouldCreateSuccessWhenPathIsRoot() {
        // given
        String path = "/";

        // when
        SegmentGroup group = SegmentGroup.of(path);

        // then
        assertTrue(group.isEmpty());
        assertEquals("/", group.getSuffix());
    }

    @Test
    void shouldCreateSuccessWhenPathWithParams() {
        // given
        String path = "/{a}/b{c}/{d}e/f{g.h}i/j";

        // when
        SegmentGroup group = SegmentGroup.of(path);

        // then
        assertEquals(4, group.getPathSegments().size());
        assertEquals("i/j", group.getSuffix());
        // 0
        assertNull(group.getPathSegments().get(0).getKey());
        assertEquals("a", group.getPathSegments().get(0).getValue());
        assertFalse(group.getPathSegments().get(0).isQuery());
        // 1
        assertEquals("/b", group.getPathSegments().get(1).getPrefix());
        assertNull(group.getPathSegments().get(1).getKey());
        assertEquals("c", group.getPathSegments().get(1).getValue());
        assertFalse(group.getPathSegments().get(1).isQuery());
        // 2
        assertEquals("/", group.getPathSegments().get(2).getPrefix());
        assertNull(group.getPathSegments().get(2).getKey());
        assertEquals("d", group.getPathSegments().get(2).getValue());
        assertFalse(group.getPathSegments().get(2).isQuery());
        // 3
        assertEquals("e/f", group.getPathSegments().get(3).getPrefix());
        assertNull(group.getPathSegments().get(3).getKey());
        assertEquals("g.h", group.getPathSegments().get(3).getValue());
        assertFalse(group.getPathSegments().get(3).isQuery());
    }

    @Test
    void shouldCreateSuccessWhenPathWithQueries() {
        // given
        String path = "a={b}&c={d.e}";

        // when
        SegmentGroup group = SegmentGroup.of(path);

        // then
        assertEquals(2, group.getQuerySegments().size());
        assertEquals("", group.getSuffix());
        // 0
        assertNull(group.getQuerySegments().get(0).getPrefix());
        assertEquals("a", group.getQuerySegments().get(0).getKey());
        assertEquals("b", group.getQuerySegments().get(0).getValue());
        assertTrue(group.getQuerySegments().get(0).isQuery());
        // 1
        assertNull(group.getQuerySegments().get(1).getPrefix());
        assertEquals("c", group.getQuerySegments().get(1).getKey());
        assertEquals("d.e", group.getQuerySegments().get(1).getValue());
        assertTrue(group.getQuerySegments().get(0).isQuery());
    }

    @Test
    void shouldCreateSuccessWhenPathWithParamsAndQueries() {
        // given
        String path = "a/{b}/c?e={f}&g=h&i={j}";

        // when
        SegmentGroup group = SegmentGroup.of(path);

        // then
        assertEquals(1, group.getPathSegments().size());
        assertEquals(3, group.getQuerySegments().size());
        assertEquals("/c", group.getSuffix());
        // 0
        assertEquals("a/", group.getPathSegments().get(0).getPrefix());
        assertNull(group.getPathSegments().get(0).getKey());
        assertEquals("b", group.getPathSegments().get(0).getValue());
        assertFalse(group.getPathSegments().get(0).isQuery());
        // 0
        assertEquals("e", group.getQuerySegments().get(0).getKey());
        assertEquals("f", group.getQuerySegments().get(0).getValue());
        assertTrue(group.getQuerySegments().get(0).isAccessor());
        assertTrue(group.getQuerySegments().get(0).isQuery());
        // 1
        assertEquals("g", group.getQuerySegments().get(1).getKey());
        assertEquals("h", group.getQuerySegments().get(1).getValue());
        assertFalse(group.getQuerySegments().get(1).isAccessor());
        assertTrue(group.getQuerySegments().get(1).isQuery());
        // 2
        assertEquals("i", group.getQuerySegments().get(2).getKey());
        assertEquals("j", group.getQuerySegments().get(2).getValue());
        assertTrue(group.getQuerySegments().get(2).isAccessor());
        assertTrue(group.getQuerySegments().get(2).isQuery());
    }

    @Test
    void shouldGroupIsEmpty() {
        SegmentGroup group = SegmentGroup.of("/");
        assertTrue(group.isEmpty());
    }
}
