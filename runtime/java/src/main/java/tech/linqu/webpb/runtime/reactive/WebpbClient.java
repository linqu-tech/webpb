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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbUtils;
import tech.linqu.webpb.runtime.common.MessageContext;

/**
 * Webpb http client to send a {@link WebpbMessage} and receive a response.
 */
public class WebpbClient {

    private final WebClient webClient;

    private final Consumer<Map<String, Object>> attributes;

    private final ObjectMapper urlObjectMapper = WebpbUtils.createUrlObjectMapper();

    protected TransportMapper transportMapper = new JsonTransportMapper();

    /**
     * WebpbClient constructor.
     *
     * @param webClient {@link WebpbClient}
     */
    public WebpbClient(WebClient webClient) {
        this(webClient, map -> {
        });
    }

    /**
     * WebpbClient constructor.
     *
     * @param webClient  {@link WebpbClient}
     * @param attributes attributes for the client
     */
    public WebpbClient(WebClient webClient, Consumer<Map<String, Object>> attributes) {
        this.webClient = webClient;
        this.attributes = attributes;
    }

    /**
     * Update {@link TransportMapper}.
     *
     * @param transportMapper {@link TransportMapper}
     */
    public void setTransportMapper(TransportMapper transportMapper) {
        this.transportMapper = transportMapper;
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
        return Mono
            .just(transportMapper.writeValue(message))
            .flatMap(body -> {
                String url = WebpbUtils.formatUrl(urlObjectMapper, message);
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
                    .map(data -> transportMapper.readValue(data, responseType));
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
        return WebpbUtils.formatUrl(baseUrl, this.urlObjectMapper, message);
    }

    /**
     * See also {@link WebpbUtils#formatUrl(URL, ObjectMapper, WebpbMessage)}.
     *
     * @param message {@link WebpbMessage}
     * @return formatted url
     */
    public String formatUrl(WebpbMessage message) {
        return WebpbUtils.formatUrl(null, this.urlObjectMapper, message);
    }
}
