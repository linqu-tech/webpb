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

import static tech.linqu.webpb.commons.Utils.uncheckedCall;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbUtils;
import tech.linqu.webpb.runtime.common.InQuery;
import tech.linqu.webpb.runtime.common.MessageContext;

/**
 * Webpb http client to send a {@link WebpbMessage} and receive a response.
 */
public class WebpbClient {

    private final WebClient webClient;

    private final URL baseUrl;

    private final Consumer<Map<String, Object>> attributes;

    private final ObjectMapper formatMapper = new ObjectMapper();

    /**
     * {@link ObjectMapper} used when send request and receive response.
     */
    protected final ObjectMapper objectMapper = createObjectMapper();

    /**
     * WebpbClient constructor.
     *
     * @param webClient {@link WebpbClient}
     * @param baseUrl   {@link URL}
     */
    public WebpbClient(WebClient webClient, URL baseUrl) {
        this(webClient, baseUrl, map -> {
        });
    }

    /**
     * WebpbClient constructor.
     *
     * @param webClient  {@link WebpbClient}
     * @param baseUrl    {@link URL}
     * @param attributes attributes for the client
     */
    public WebpbClient(WebClient webClient, URL baseUrl, Consumer<Map<String, Object>> attributes) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.attributes = attributes;
    }

    /**
     * Create an {@link ObjectMapper}.
     *
     * @return {@link ObjectMapper}
     */
    protected ObjectMapper createObjectMapper() {
        return new ObjectMapper()
            .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
            .setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                @Override
                public boolean hasIgnoreMarker(AnnotatedMember m) {
                    return super.hasIgnoreMarker(m) || m.hasAnnotation(InQuery.class);
                }
            });
    }

    /**
     * Send request and receive an expected type of response.
     *
     * @param message      {@link WebpbMessage}
     * @param responseType class of response
     * @param <T>          typing of response message
     * @return expected response with type T
     */
    public <T extends WebpbMessage> T request(WebpbMessage message, Class<T> responseType) {
        return requestAsync(message, responseType).block();
    }

    /**
     * Async request, see also {@link #request}.
     *
     * @param message      {@link WebpbMessage}
     * @param responseType class of response
     * @param <T>          typing of response message
     * @return expected response with type T
     */
    public <T extends WebpbMessage> Mono<T> requestAsync(WebpbMessage message,
                                                         Class<T> responseType) {
        MessageContext context = WebpbUtils.getContext(message);
        if (context == null) {
            throw new RuntimeException("Request method and path is required");
        }
        return Mono
            .just(uncheckedCall(() -> objectMapper.writeValueAsString(message)))
            .flatMap(body -> {
                String url = WebpbUtils.formatUrl(baseUrl, formatMapper, message);
                return webClient
                    .method(context.getMethod())
                    .uri(url)
                    .bodyValue(body)
                    .attributes(attributes)
                    .retrieve()
                    .onStatus(
                        httpStatus -> httpStatus.series() != HttpStatus.Series.SUCCESSFUL,
                        this::createException
                    )
                    .bodyToMono(byte[].class)
                    .map(data -> uncheckedCall(() -> objectMapper.readValue(data, responseType)));
            });
    }

    /**
     * Create an exception from {@link ClientResponse}.
     *
     * @param clientResponse {@link ClientResponse}
     * @return mono of {@link Throwable}
     */
    protected Mono<? extends Throwable> createException(ClientResponse clientResponse) {
        return clientResponse.createException();
    }

    /**
     * See also {@link WebpbUtils#formatUrl(URL, ObjectMapper, WebpbMessage)}.
     *
     * @param baseUrl {@link URL}
     * @param message {@link WebpbMessage}
     * @return formatted url
     */
    public String formatUrl(URL baseUrl, WebpbMessage message) {
        return WebpbUtils.formatUrl(baseUrl, this.formatMapper, message);
    }

    /**
     * See also {@link WebpbUtils#formatUrl(URL, ObjectMapper, WebpbMessage)}.
     *
     * @param message {@link WebpbMessage}
     * @return formatted url
     */
    public String formatUrl(WebpbMessage message) {
        return WebpbUtils.formatUrl(null, this.formatMapper, message);
    }
}
