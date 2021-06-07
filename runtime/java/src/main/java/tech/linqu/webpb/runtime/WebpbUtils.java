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
import static tech.linqu.webpb.commons.Utils.emptyOrDefault;
import static tech.linqu.webpb.commons.Utils.uncheckedCall;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpMethod;
import tech.linqu.webpb.commons.ParamGroup;
import tech.linqu.webpb.commons.PathParam;
import tech.linqu.webpb.runtime.common.MessageContext;

/**
 * Utilities for webpb java runtime.
 */
public class WebpbUtils {

    private static final Map<Class<?>, MessageContext> contextCache = new ConcurrentHashMap<>();

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
     * @param objectMapper objectMapper to extract message properties
     * @param message      {@link WebpbMessage}
     * @return formatted url
     */
    public static String formatUrl(ObjectMapper objectMapper, WebpbMessage message) {
        MessageContext context = getContext(message);
        if (context.getParamGroup().isEmpty()) {
            return context.getPath();
        }
        JsonNode data = objectMapper.convertValue(message, JsonNode.class);
        String path = formatPath(data, context.getParamGroup(), null);
        return emptyOrDefault(context.getContext(), "") + path;
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
        if (context.getParamGroup().isEmpty()) {
            return concatUrl(baseUrl, context.getPath());
        }
        JsonNode data = objectMapper.convertValue(message, JsonNode.class);
        String path = formatPath(data, context.getParamGroup(), baseUrl.getQuery());
        String file =
            emptyOrDefault(baseUrl.getPath(), "") + emptyOrDefault(context.getContext(), "") + path;
        return concatUrl(baseUrl, file);
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
                .setParamGroup(ParamGroup.of(meta.getPath()));
        });
        if (context == MessageContext.NULL_CONTEXT) {
            throw new RuntimeException("Invalid meta method or meta path.");
        }
        return context;
    }

    private static String formatPath(JsonNode data, ParamGroup paramGroup, String query) {
        StringBuilder builder = new StringBuilder();
        Iterator<PathParam> iterator = paramGroup.getParams().iterator();
        String link;
        while (iterator.hasNext()) {
            PathParam param = iterator.next();
            builder.append(param.getPrefix());
            if (builder.charAt(builder.length() - 1) == '?') {
                builder.deleteCharAt(builder.length() - 1);
            }
            if (hasLength(param.getKey())) {
                if (hasLength(query)) {
                    builder.append("?").append(query);
                    link = "&";
                } else {
                    link = "?";
                }
                do {
                    param = param == null ? iterator.next() : param;
                    String value = resolve(data, param.getAccessor());
                    if (hasLength(value)) {
                        builder.append(link).append(param.getKey()).append("=").append(value);
                        link = "&";
                    }
                    param = null;
                } while (iterator.hasNext());
                break;
            }
            String value = resolve(data, param.getAccessor());
            if (!hasLength(value)) {
                throw new RuntimeException(
                    String.format("Path variable '%s' not found", param.getAccessor()));
            }
            builder.append(value);
        }
        if (hasLength(paramGroup.getSuffix())) {
            builder.append(paramGroup.getSuffix());
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
}
