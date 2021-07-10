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

import static org.springframework.util.StringUtils.hasLength;
import static tech.linqu.webpb.commons.Utils.orEmpty;
import static tech.linqu.webpb.commons.Utils.uncheckedCall;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tech.linqu.webpb.commons.SegmentGroup;
import tech.linqu.webpb.commons.UrlSegment;
import tech.linqu.webpb.runtime.common.MessageContext;

/**
 * Utilities for webpb java runtime.
 */
public class WebpbUtils {

    private static final Map<Class<?>, MessageContext> contextCache = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private WebpbUtils() {
    }

    /**
     * Read WebpbMeta from a webpb message.
     *
     * @param type Class
     * @return WebpbMeta
     */
    public static WebpbMeta readWebpbMeta(Class<? extends WebpbMessage> type) {
        try {
            Field field = type.getDeclaredField("WEBPB_META");
            return (WebpbMeta) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Format request url from API base url and {@link WebpbMeta}.
     *
     * @param message {@link WebpbMessage}
     * @return formatted url
     */
    public static String formatUrl(WebpbMessage message) {
        return formatUrl(objectMapper, message);
    }

    /**
     * Format request url from API base url and {@link WebpbMeta}.
     *
     * @param objectMapper objectMapper to extract message properties
     * @param message      {@link WebpbMessage}
     * @return formatted url
     */
    public static String formatUrl(ObjectMapper objectMapper, WebpbMessage message) {
        MessageContext context = getContext(message);
        String path = context.getPath();
        if (!context.getSegmentGroup().isEmpty()) {
            JsonNode data = objectMapper.convertValue(message, JsonNode.class);
            path = formatPath(data, context.getSegmentGroup(), null);
        }
        if (!path.startsWith("/")) {
            return path;
        }
        return joinPath(context.getContext(), path);
    }

    /**
     * Format request url from API base url and {@link WebpbMeta}.
     *
     * @param baseUrl      {@link URL}
     * @param objectMapper objectMapper to extract message properties
     * @param message      {@link WebpbMessage}
     * @return formatted url
     */
    public static String formatUrl(URL baseUrl, ObjectMapper objectMapper, WebpbMessage message) {
        if (baseUrl == null) {
            return formatUrl(objectMapper, message);
        }
        MessageContext context = getContext(message);
        if (!context.getPath().startsWith("/")) {
            throw new RuntimeException(String
                .format("Can not concat baseUrl: %s with path: %s", baseUrl, context.getPath()));
        }
        String path = context.getPath();
        if (!context.getSegmentGroup().isEmpty()) {
            JsonNode data = objectMapper.convertValue(message, JsonNode.class);
            path = formatPath(data, context.getSegmentGroup(), baseUrl.getQuery());
        }
        return concatUrl(baseUrl, joinPath(baseUrl.getPath(), context.getContext(), path));
    }

    private static String concatUrl(URL baseUrl, String file) {
        if ("/".equals(file)) {
            return baseUrl.toString();
        }
        URL url = uncheckedCall(
            () -> new URL(baseUrl.getProtocol(), baseUrl.getHost(), baseUrl.getPort(), file, null));
        return url.toString();
    }

    /**
     * Test if a path is valid for webpb.
     *
     * @param path path to test
     * @return true if valid
     */
    public static boolean isValidPath(String path) {
        if (path == null) {
            return false;
        }
        if (!hasLength(path) || "/".equals(path)) {
            return true;
        }
        if (path.startsWith("/") && path.contains("//")) {
            return false;
        }
        try {
            new URL(path.startsWith("/") ? "https://a" + path : path);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    /**
     * Clear context cache.
     */
    public static void clearContextCache() {
        contextCache.clear();
    }

    /**
     * Get or create context for {@link WebpbMessage}.
     *
     * @param message {@link WebpbMessage}
     * @return {@link MessageContext}
     */
    public static MessageContext getContext(WebpbMessage message) {
        MessageContext context = contextCache.computeIfAbsent(message.getClass(), k -> {
            WebpbMeta meta = message.webpbMeta();
            if (meta == null) {
                return MessageContext.NULL_CONTEXT;
            }
            if (!hasLength(meta.getMethod())) {
                return MessageContext.NULL_CONTEXT;
            }
            if (!isValidPath(meta.getPath())) {
                return MessageContext.NULL_CONTEXT;
            }
            return new MessageContext()
                .setMethod(HttpMethod.valueOf(meta.getMethod().toUpperCase()))
                .setContext(meta.getContext())
                .setPath(meta.getPath())
                .setSegmentGroup(SegmentGroup.of(meta.getPath()));
        });
        if (context == MessageContext.NULL_CONTEXT) {
            throw new RuntimeException("Invalid meta method or meta path.");
        }
        return context;
    }

    private static String formatPath(JsonNode data, SegmentGroup group, String query) {
        StringBuilder builder = new StringBuilder();
        for (UrlSegment segment : group.getPathSegments()) {
            builder.append(segment.getPrefix());
            String value = resolve(data, segment.getValue());
            if (!hasLength(value)) {
                throw new RuntimeException(
                    String.format("Path variable '%s' not found", segment.getValue()));
            }
            builder.append(value);
        }
        builder.append(group.getSuffix());
        String link = "?";
        if (StringUtils.hasLength(query)) {
            builder.append("?").append(query);
            link = "&";
        }
        for (UrlSegment segment : group.getQuerySegments()) {
            if (segment.isAccessor()) {
                String value = resolve(data, segment.getValue());
                if (hasLength(value) && !"null".equals(value)) {
                    builder.append(link).append(segment.getKey()).append("=").append(value);
                    link = "&";
                }
            } else if (StringUtils.hasLength(segment.getKey())) {
                builder.append(link).append(segment.getKey())
                    .append("=").append(segment.getValue());
                link = "&";
            } else {
                builder.append(link).append(segment.getValue());
                link = "&";
            }
        }
        return builder.toString();
    }

    private static String resolve(JsonNode jsonNode, String accessor) {
        for (String part : accessor.split("\\.")) {
            jsonNode = jsonNode.get(part);
            if (jsonNode == null) {
                return null;
            }
        }
        return jsonNode.asText();
    }

    /**
     * Update a message extends from {@link WebpbMessage}.
     *
     * @param message      message extends from {@link WebpbMessage}
     * @param variablesMap map of variables
     * @param <T>          type extends from {@link WebpbMessage}
     * @return T
     */
    public static <T extends WebpbMessage> T updateMessage(T message,
                                                           Map<String, String> variablesMap) {
        if (CollectionUtils.isEmpty(variablesMap)) {
            return message;
        }

        WebpbMeta meta = message.webpbMeta();
        if (meta == null) {
            return message;
        }
        SegmentGroup group = SegmentGroup.of(meta.getPath());
        ObjectNode objectNode = objectMapper.createObjectNode();
        for (UrlSegment segment : group.getSegments()) {
            if (!segment.isAccessor()) {
                continue;
            }
            String key = segment.getKey();
            String accessor = segment.getValue();
            String value = variablesMap.get(StringUtils.hasLength(key) ? key : accessor);
            if (value != null) {
                String[] accessors = accessor.split("\\.");
                ObjectNode targetNode = findNode(objectNode, accessors);
                targetNode.put(accessors[accessors.length - 1], value);
            }
        }
        try {
            return objectMapper.readerForUpdating(message).readValue(objectNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectNode findNode(ObjectNode objectNode, String[] accessors) {
        for (int i = 0; i < accessors.length - 1; i++) {
            String accessor = accessors[i];
            ObjectNode subNode = (ObjectNode) objectNode.get(accessor);
            if (subNode == null) {
                subNode = objectMapper.createObjectNode();
                objectNode.set(accessor, subNode);
            }
            objectNode = subNode;
        }
        return objectNode;
    }

    private static String joinPath(String... segments) {
        if (segments == null || segments.length == 0) {
            return "/";
        }
        StringBuilder builder = new StringBuilder();
        for (String segment : segments) {
            segment = orEmpty(segment);
            trimSlash(builder);
            if (segment.startsWith("/")) {
                builder.append(segment);
            } else {
                builder.append("/").append(segment);
            }
        }
        trimSlash(builder);
        return URI.create(builder.toString()).normalize().toString();
    }

    private static void trimSlash(StringBuilder builder) {
        if (builder.length() > 0 && builder.charAt(builder.length() - 1) == '/') {
            builder.deleteCharAt(builder.length() - 1);
        }
    }
}
