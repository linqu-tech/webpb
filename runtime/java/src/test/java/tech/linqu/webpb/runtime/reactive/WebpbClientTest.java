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

package tech.linqu.webpb.runtime.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tech.linqu.webpb.runtime.WebpbMeta;
import tech.linqu.webpb.runtime.WebpbUtils;
import tech.linqu.webpb.runtime.model.BadRequest;
import tech.linqu.webpb.runtime.model.FooRequest;
import tech.linqu.webpb.runtime.model.FooResponse;

class WebpbClientTest {

    private final WebClient.Builder builder = WebClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    @Test
    void shouldRequestSuccess() {
        WebClient webClient = WebClient.builder()
            .exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.OK)
                    .body("{\"id\": \"123\"}")
                    .build())
            )
            .build();
        WebpbUtils.clearContextCache();
        WebpbClient client = new WebpbClient(webClient);
        FooResponse response = client.request(new FooRequest(), FooResponse.class);
        assertEquals(123, response.getId());
    }

    @Test
    void shouldThrowExceptionWhenResponseWithError() {
        WebClient webClient = WebClient.builder()
            .exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST).build())
            )
            .build();
        WebpbUtils.clearContextCache();
        WebpbClient client = new WebpbClient(webClient);
        assertThrows(WebClientResponseException.class,
            () -> client.request(new FooRequest(), FooResponse.class));
    }

    @Test
    void shouldThrowExceptionWhenRequestWithoutContext() {
        WebpbUtils.clearContextCache();
        WebpbClient client = new WebpbClient(mock(WebClient.class));
        assertThrows(RuntimeException.class,
            () -> client.request(new BadRequest(), FooResponse.class));
    }

    @Test
    void shouldThrowExceptionWhenRequestWithoutMethod() {
        WebpbUtils.clearContextCache();
        WebpbClient client = new WebpbClient(mock(WebClient.class));
        FooRequest request = new FooRequest(WebpbMeta.builder().path("path").build());
        assertThrows(RuntimeException.class, () -> client.request(request, FooResponse.class));
    }

    @Test
    void shouldThrowExceptionWhenRequestWithoutPath() {
        WebpbUtils.clearContextCache();
        WebpbClient client = new WebpbClient(mock(WebClient.class));
        FooRequest request = new FooRequest(WebpbMeta.builder().method("method").build());
        assertThrows(RuntimeException.class, () -> client.request(request, FooResponse.class));
    }

    @Test
    void shouldFormatUrlSuccess() {
        WebpbUtils.clearContextCache();
        WebpbClient webpbClient = new WebpbClient(builder.build());
        String url = webpbClient.formatUrl(new FooRequest());
        assertEquals("/domain/123/action?pagination=true&size=20&page=10", url);
    }

    @Test
    void shouldFormatUrlSuccessWhenWithBaseUrl() throws MalformedURLException {
        WebpbUtils.clearContextCache();
        WebpbClient webpbClient = new WebpbClient(builder.build());
        String url = webpbClient.formatUrl(new URL("https://abc"), new FooRequest());
        assertEquals("https://abc/domain/123/action?pagination=true&size=20&page=10", url);
    }

    @Test
    void shouldFormatUrlThrowExceptionGivenPathIsUrlWhenWithBaseUrl() {
        WebpbUtils.clearContextCache();
        WebpbClient webpbClient = new WebpbClient(builder.build());
        FooRequest request =
            new FooRequest(WebpbMeta.builder().method("GET").path("https://domain").build());
        assertThrows(RuntimeException.class,
            () -> webpbClient.formatUrl(new URL("https://abc"), request));
    }
}
