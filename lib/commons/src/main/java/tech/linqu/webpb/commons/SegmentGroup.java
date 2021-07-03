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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Group of {@link UrlSegment} captured from url.
 */
public class SegmentGroup {

    private static final String KEY = "key";

    private static final String VALUE = "value";

    private static final Pattern PATH_PATTERN = Pattern.compile("(?<value>\\{[^/]+})");

    private static final Pattern QUERY_PATTERN =
        Pattern.compile("((?<key>\\w+)=)?(?<value>[^/?&]+)&?");

    private final List<UrlSegment> pathSegments = new ArrayList<>();

    private String suffix = "";

    private final List<UrlSegment> querySegments = new ArrayList<>();

    private final List<UrlSegment> segments = new ArrayList<>();

    /**
     * Static creator.
     *
     * @param url request url
     * @return Params group
     */
    public static SegmentGroup of(String url) {
        SegmentGroup group = new SegmentGroup();
        if (url == null || url.isEmpty()) {
            return group;
        }
        String[] parts = url.split("\\?");
        String path = parts[0];
        String query = null;
        if (parts.length == 2) {
            query = parts[1];
        } else if (path.contains("=")) {
            path = null;
            query = parts[0];
        }

        if (path != null) {
            Matcher pathMatcher = PATH_PATTERN.matcher(path);
            int index = 0;
            while (pathMatcher.find()) {
                UrlSegment segment = new UrlSegment(
                    path.substring(index, pathMatcher.start()), null, pathMatcher.group(VALUE));
                group.pathSegments.add(segment);
                index = pathMatcher.end();
            }
            group.suffix = path.substring(index);
        }

        if (query != null) {
            Matcher queryMatcher = QUERY_PATTERN.matcher(query);
            while (queryMatcher.find()) {
                UrlSegment segment =
                    new UrlSegment(null, queryMatcher.group(KEY), queryMatcher.group(VALUE));
                group.querySegments.add(segment);
            }
        }
        group.segments.addAll(group.pathSegments);
        group.segments.addAll(group.querySegments);
        return group;
    }

    /**
     * If params is empty.
     *
     * @return true if params is empty
     */
    public boolean isEmpty() {
        return pathSegments.isEmpty() && querySegments.isEmpty();
    }

    /**
     * Get path segments.
     *
     * @return list of {@link UrlSegment}
     */
    public List<UrlSegment> getPathSegments() {
        return pathSegments;
    }

    /**
     * Get query segments.
     *
     * @return list of {@link UrlSegment}
     */
    public List<UrlSegment> getQuerySegments() {
        return querySegments;
    }

    /**
     * Get all segments.
     *
     * @return list of segments.
     */
    public List<UrlSegment> getSegments() {
        return segments;
    }

    /**
     * Get suffix of path.
     *
     * @return suffix
     */
    public String getSuffix() {
        return suffix;
    }
}
