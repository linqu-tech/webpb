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

package tech.linqu.webpb.utilities.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.linqu.webpb.utilities.utils.Utils.normalize;

import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void shouldNormalizeStringSuccess() {
        assertEquals("", normalize(""));
        assertEquals("", normalize("/"));
        assertEquals("", normalize("//"));
        assertEquals("/a", normalize("/a"));
        assertEquals("/a", normalize("a/"));
        assertEquals("/a", normalize("/a/"));
        assertEquals("/ab.c", normalize("//ab.c"));
        assertEquals("https://ab.c", normalize("https://ab.c"));
    }
}
