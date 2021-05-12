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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * WebpbMeta
 */
public class WebpbMeta {

    private String method;

    private String context;

    private String path;

    private List<String> tags;

    /**
     * get method
     * @return String
     */
    public String getMethod() {
        return method;
    }

    /**
     * get context
     * @return String
     */
    public String getContext() {
        return context;
    }

    /**
     * get path
     * @return String
     */
    public String getPath() {
        return path;
    }

    /**
     * get tags
     * @return List
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Builder
     */
    public static class Builder {

        private String method;

        private String context;

        private String path;

        private List<String> tags = Collections.emptyList();

        /**
         * method
         * @param method String
         * @return Builder
         */
        public Builder method(String method) {
            this.method = method;
            return this;
        }

        /**
         * context
         * @param context String
         * @return Builder
         */
        public Builder context(String context) {
            this.context = context;
            return this;
        }

        /**
         * path
         * @param path String
         * @return Builder
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * tags
         * @param tags array of String
         * @return Builder
         */
        public Builder tags(String... tags) {
            this.tags = Arrays.asList(tags);
            return this;
        }

        /**
         * build
         * @return WebpbMeta
         */
        public WebpbMeta build() {
            WebpbMeta meta = new WebpbMeta();
            meta.method = this.method;
            meta.context = this.context;
            meta.path = this.path;
            meta.tags = this.tags;
            return meta;
        }
    }
}
