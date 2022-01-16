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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import tech.linqu.webpb.runtime.common.InQuery;

/**
 * JsonTransportMapper.
 */
public class JsonTransportMapper implements TransportMapper {

    private final ObjectMapper objectMapper = JsonMapper.builder()
        .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .build()
        .setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public boolean hasIgnoreMarker(AnnotatedMember m) {
                return super.hasIgnoreMarker(m) || m.hasAnnotation(InQuery.class);
            }
        });

    /**
     * Write value as string.
     *
     * @param value {@link Object}
     * @return string
     */
    @Override
    public String writeValue(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read value as object.
     *
     * @param src       byte array
     * @param valueType {@link Class}
     * @param <T>       type of T
     * @return {@link T}
     */
    @Override
    public <T> T readValue(byte[] src, Class<T> valueType) {
        try {
            return objectMapper.readValue(src, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
