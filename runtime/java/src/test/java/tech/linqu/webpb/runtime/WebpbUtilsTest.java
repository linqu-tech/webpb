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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
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
        assertTrue(WebpbUtils.isValidPath("https://abc"));
    }

    @Test
    void shouldFormatUrlSuccessWhenWithBaseUrl() throws MalformedURLException {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(new URL("https://abc"), objectMapper, new FooRequest());
        assertEquals("https://abc/domain/123/action?pagination=true&size=20&page=10", url);
    }

    @Test
    void shouldFormatUrlSuccessWhenWithoutBaseUrl() {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(null, objectMapper, new FooRequest());
        assertEquals("/domain/123/action?pagination=true&size=20&page=10", url);
    }

    @Test
    void shouldFormatUrlSuccessWhenBaseUrlWithQuery() throws MalformedURLException {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(new URL("https://a?a=1"), objectMapper, new FooRequest());
        assertEquals("https://a/domain/123/action?a=1&pagination=true&size=20&page=10", url);
    }

    @Test
    void shouldFormatUrlSuccessWhenOnlyQuery() {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(null, objectMapper, new FooRequest(
            WebpbMeta.builder().method("GET")
                .path("/pagination={pagination}&size={pageable.size}&page={pageable.page}")
                .build()));
        assertEquals("/?pagination=true&size=20&page=10", url);
    }

    @Test
    void shouldFormatUrlSuccessGivenPathIsUrlWhenWithBaseUrl() throws MalformedURLException {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(new URL("https://domain"), objectMapper,
            new FooRequest(WebpbMeta.builder().method("GET").path("/").build()));
        assertEquals("https://domain", url);
    }

    @Test
    void shouldFormatUrlSuccessGivenPathIsUrlWhenWithoutBaseUrl() {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(null, objectMapper,
            new FooRequest(WebpbMeta.builder().method("GET").path("https://domain").build()));
        assertEquals("https://domain", url);
    }

    @Test
    void shouldFormatUrlThrowExceptionGivenPathIsUrlWhenWithBaseUrl() {
        WebpbUtils.clearContextCache();
        FooRequest request =
            new FooRequest(WebpbMeta.builder().method("GET").path("https://domain").build());
        assertThrows(RuntimeException.class,
            () -> WebpbUtils.formatUrl(new URL("https://abc"), objectMapper, request));
    }

    @Test
    void shouldFormatUrlSuccessWhenRequestMissingValue() {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(null, objectMapper,
            new FooRequest(WebpbMeta.builder().method("GET").path("https://domain?a={a}").build()));
        assertEquals("https://domain", url);
    }

    @Test
    void shouldFormatUrlSuccessWhenQueryWithSuffix() {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(null, objectMapper,
            new FooRequest(
                WebpbMeta.builder().method("GET").path("https://domain?data={data}hello").build()));
        assertEquals("https://domain?data=data123hello", url);
    }

    @Test
    void shouldFormatUrlSuccessWhenQueryWithOnlySuffix() {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(null, objectMapper,
            new FooRequest(WebpbMeta.builder().method("GET").path("https://domain?hello").build()));
        assertEquals("https://domain?hello", url);
    }

    @Test
    void shouldFormatUrlSuccessWhenWithMultiplePathVariables() {
        WebpbUtils.clearContextCache();
        String url = WebpbUtils.formatUrl(null, objectMapper,
            new FooRequest(WebpbMeta.builder().method("GET")
                .path("https://{pagination}/{pageable.page}/{pageable.size}").build()));
        assertEquals("https://true/10/20", url);
    }

    @Test
    void shouldThrowExceptionWhenPathVariableNotExists() {
        WebpbUtils.clearContextCache();
        assertThrows(RuntimeException.class,
            () -> WebpbUtils.formatUrl(null, objectMapper,
                new FooRequest(WebpbMeta.builder().method("GET").path("https://a/{b}/c").build())),
            "Path variable 'a' not found");
    }
}

