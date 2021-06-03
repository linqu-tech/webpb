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
public class PathParam {

    private final String prefix;

    private final String key;

    private final String accessor;

    /**
     * Construct a {@link PathParam}.
     *
     * @param prefix   prefix string before this param
     * @param key      key of the param
     * @param accessor accessor to resolve value
     */
    public PathParam(String prefix, String key, String accessor) {
        this.prefix = prefix;
        this.key = key;
        this.accessor = accessor;
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
     * Get accessor.
     *
     * @return accessor
     */
    public String getAccessor() {
        return accessor;
    }
}
