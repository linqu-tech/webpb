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
import lombok.Getter;
import lombok.ToString;

/**
 * Meta data for a {@link WebpbMessage}.
 */
@Getter
@ToString
public class WebpbMeta {

    private String method;

    private String context;

    private String path;

    private List<String> tags;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link WebpbMeta}.
     */
    public static class Builder {

        private String method;

        private String context;

        private String path;

        private List<String> tags = Collections.emptyList();

        /**
         * Set http method name.
         *
         * @param method http method
         * @return {@link Builder}
         */
        public Builder method(String method) {
            this.method = method;
            return this;
        }

        /**
         * Set server context.
         *
         * @param context server context
         * @return {@link Builder}
         */
        public Builder context(String context) {
            this.context = context;
            return this;
        }

        /**
         * Set request path.
         *
         * @param path request path
         * @return {@link Builder}
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Set message tags.
         *
         * @param tags tag array
         * @return {@link Builder}
         */
        public Builder tags(String... tags) {
            this.tags = Arrays.asList(tags);
            return this;
        }

        /**
         * Build the {@link WebpbMeta}.
         *
         * @return {@link WebpbMeta}
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
