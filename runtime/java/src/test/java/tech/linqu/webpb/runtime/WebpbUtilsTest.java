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

package tech.linqu.webpb.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.runtime.model.BadRequest;
import tech.linqu.webpb.runtime.model.FooRequest;

class WebpbUtilsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReadWebpbMetaSuccess() {
        assertNotNull(WebpbUtils.readWebpbMeta(FooRequest.class));
    }

    @Test
    void shouldReturnNullWhenWepbMetaNotExists() {
        assertNull(WebpbUtils.readWebpbMeta(BadRequest.class));
    }

    @Test
    void isValidPathTest() {
        assertFalse(WebpbUtils.isValidPath(null));
        assertFalse(WebpbUtils.isValidPath("//"));
        assertFalse(WebpbUtils.isValidPath("abc"));
        assertTrue(WebpbUtils.isValidPath(""));
        assertTrue(WebpbUtils.isValidPath("/ /"));
        assertTrue(WebpbUtils.isValidPath("/"));
        assertTrue(WebpbUtils.isValidPath("/abc"));
        assertTrue(WebpbUtils.isValidPath("https:/abc"));
    }

    @Test
    void shouldFormatUrlSuccessWhenWithBaseUrl() throws MalformedURLException {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(new URL("https://abc"), objectMapper, new FooRequest());
        assertEquals("https://abc/domain/123/action?pagination=true&size=20&page=10", url);
    }
}
