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

package tech.linqu.webpb.java.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Name;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.JavaFileOpts;
import tech.linqu.webpb.utilities.utils.OptionUtils;

/**
 * Store names for importing.
 */
public class NameMap {

    private static final JavaParser JAVA_PARSER = new JavaParser();

    private final Map<String, Name> nameMap = new HashMap<>();

    /**
     * Parse imports from file descriptors.
     *
     * @param descriptors {@link FileDescriptor} array.
     */
    public NameMap(List<FileDescriptor> descriptors) {
        JAVA_PARSER.parseName(List.class.getName())
            .ifSuccessful(name -> nameMap.put(name.getIdentifier(), name));
        JAVA_PARSER.parseName(Map.class.getName())
            .ifSuccessful(name -> nameMap.put(name.getIdentifier(), name));

        addImports(descriptors);
    }

    private void addImports(List<FileDescriptor> descriptors) {
        for (FileDescriptor fileDescriptor : descriptors) {
            String javaPackage = fileDescriptor.getOptions().getJavaPackage();

            if (StringUtils.isNotEmpty(javaPackage)) {
                for (Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                    String identifier = descriptor.getName();
                    nameMap.put(identifier, new Name(new Name(null, javaPackage), identifier));
                }
                for (Descriptors.EnumDescriptor descriptor : fileDescriptor.getEnumTypes()) {
                    String identifier = descriptor.getName();
                    nameMap.put(identifier, new Name(new Name(null, javaPackage), identifier));
                }
            }

            JavaFileOpts fileOpts =
                OptionUtils.getOpts(fileDescriptor, FileOpts::hasJava).getJava();
            for (String str : fileOpts.getImportList()) {
                JAVA_PARSER.parseName(str).ifSuccessful(name ->
                    nameMap.put(name.getIdentifier(), name)
                );
            }
            addImports(fileDescriptor.getDependencies());
        }
    }

    /**
     * Get full name of a name or simple name.
     *
     * @param name name or simple name.
     * @return full name or {@link RuntimeException}
     */
    public Name getFullName(Name name) {
        if (name.getQualifier().isPresent()) {
            return new Name(name.getQualifier().orElse(null), name.getIdentifier());
        }
        Name full = nameMap.get(name.getIdentifier());
        if (full == null) {
            throw new RuntimeException(
                String.format("Unknown identifier %s is not imported", name.getIdentifier()));
        }
        return new Name(full.getQualifier().orElse(null), full.getIdentifier());
    }

    public Optional<Name> getName(String identifier) {
        return Optional.ofNullable(nameMap.get(identifier));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Name> entry : nameMap.entrySet()) {
            builder.append(entry.getKey())
                .append(" -> ")
                .append(entry.getValue())
                .append("\n");
        }
        return builder.toString();
    }
}
