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

/**
 * Path param captured from url.
 */
public class UrlSegment {

    private final boolean accessor;

    private final String prefix;

    private final String key;

    private final String value;

    /**
     * Construct a {@link UrlSegment}.
     *
     * @param prefix prefix of the segment
     * @param key    key of the param
     * @param value  value of the segment
     */
    public UrlSegment(String prefix, String key, String value) {
        this.prefix = prefix;
        this.key = key;
        this.accessor = value.startsWith("{") && value.endsWith("}");
        if (this.accessor) {
            this.value = value.substring(1, value.length() - 1);
        } else {
            this.value = value;
        }
    }

    /**
     * Is query.
     *
     * @return is in query
     */
    public boolean isQuery() {
        return key != null && !key.isEmpty();
    }

    /**
     * Is accessor.
     *
     * @return accessor
     */
    public boolean isAccessor() {
        return accessor;
    }

    /**
     * Get prefix.
     *
     * @return prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get key.
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }
}
