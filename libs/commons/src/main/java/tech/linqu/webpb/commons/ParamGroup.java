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
 * Group of {@link PathParam} captured from url.
 */
public class ParamGroup {

    public static final String QUERY_KEY = "key";

    public static final String QUERY_ACCESSOR = "accessor";

    public static final String QUERY_PATTERN = "((?<key>\\w+)=)?\\{(?<accessor>[\\w.]+)}&?";

    private final List<PathParam> params = new ArrayList<>();

    private String suffix = "";

    /**
     * Static creator.
     *
     * @param path request path
     * @return Params group
     */
    public static ParamGroup of(String path) {
        ParamGroup group = new ParamGroup();
        if (path == null || path.isEmpty()) {
            return group;
        }
        Pattern pattern = Pattern.compile(QUERY_PATTERN);
        Matcher matcher = pattern.matcher(path);

        int index = 0;
        while (matcher.find()) {
            PathParam param = new PathParam(
                path.substring(index, matcher.start()),
                matcher.group(QUERY_KEY),
                matcher.group(QUERY_ACCESSOR)
            );
            group.params.add(param);
            index = matcher.end();
        }
        group.suffix = path.substring(index);
        return group;
    }

    public List<PathParam> getParams() {
        return params;
    }

    public String getSuffix() {
        return suffix;
    }
}
