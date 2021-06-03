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

package tech.linqu.webpb.ts.generator;

import static com.google.protobuf.Descriptors.EnumDescriptor;
import static com.google.protobuf.Descriptors.EnumValueDescriptor;
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.LONG;
import static com.google.protobuf.Descriptors.FileDescriptor;
import static tech.linqu.webpb.utilities.utils.OptionUtils.getOpts;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.commons.ParamGroup;
import tech.linqu.webpb.commons.PathParam;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.MessageOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptEnumOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptFieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptMessageOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.TsFieldOpts;
import tech.linqu.webpb.utilities.utils.Const;
import tech.linqu.webpb.utilities.utils.DescriptorUtils;
import tech.linqu.webpb.utilities.utils.OptionUtils;
import tech.linqu.webpb.utilities.utils.Utils;

/**
 * Generator to process {@link Descriptor}.
 */
@RequiredArgsConstructor(staticName = "of")
public final class Generator {

    private static final Map<FieldDescriptor.Type, String> TYPES;

    static {
        Map<FieldDescriptor.Type, String> map = new HashMap<>();
        map.put(FieldDescriptor.Type.BOOL, "boolean");
        map.put(FieldDescriptor.Type.BYTES, "Uint8Array");
        map.put(FieldDescriptor.Type.DOUBLE, "number");
        map.put(FieldDescriptor.Type.FLOAT, "number");
        map.put(FieldDescriptor.Type.FIXED32, "number");
        map.put(FieldDescriptor.Type.FIXED64, "number");
        map.put(FieldDescriptor.Type.INT32, "number");
        map.put(FieldDescriptor.Type.INT64, "number");
        map.put(FieldDescriptor.Type.SFIXED32, "number");
        map.put(FieldDescriptor.Type.SFIXED64, "number");
        map.put(FieldDescriptor.Type.SINT32, "number");
        map.put(FieldDescriptor.Type.SINT64, "number");
        map.put(FieldDescriptor.Type.STRING, "string");
        map.put(FieldDescriptor.Type.UINT32, "number");
        map.put(FieldDescriptor.Type.UINT64, "number");
        TYPES = map;
    }

    private static final String INDENT = "  ";

    private final RequestContext requestContext;

    private final FileDescriptor fileDescriptor;

    private final List<String> tags;

    private final StringBuilder builder = new StringBuilder();

    private final Set<String> imports = new HashSet<>();

    private int level = 0;

    /**
     * Entrance of the generator.
     *
     * @return {@link StringBuilder}
     */
    public StringBuilder generate() {
        String packageName = fileDescriptor.getName();
        if (generateTypes()) {
            StringBuilder builder = new StringBuilder();
            builder.append("// " + Const.HEADER + "\n");
            builder.append("// " + Const.GIT_URL + "\n\n");
            builder.append("import * as Webpb from 'webpb';\n\n");
            for (String type : imports) {
                if (!StringUtils.startsWith(type, packageName)) {
                    builder.append("import * as ").append(type)
                        .append(" from './").append(type).append("';\n\n");
                }
            }
            this.builder.insert(0, builder);
        }
        return this.builder;
    }

    private boolean generateTypes() {
        boolean hasContent = false;
        for (EnumDescriptor enumDescriptor : fileDescriptor.getEnumTypes()) {
            OptEnumOpts optEnumOpts = getOpts(enumDescriptor, EnumOpts::hasOpt).getOpt();
            if (OptionUtils.shouldSkip(optEnumOpts.getTagList(), this.tags)) {
                continue;
            }
            hasContent = true;
            generateEnum(enumDescriptor);
        }
        hasContent |= handleDescriptors(fileDescriptor.getMessageTypes());
        if (level == 0) {
            trimDuplicatedNewline();
        }
        return hasContent;
    }

    private boolean handleDescriptors(List<Descriptor> descriptors) {
        boolean hasContent = false;
        for (Descriptor descriptor : descriptors) {
            OptMessageOpts messageOpts = getOpts(descriptor, MessageOpts::hasOpt).getOpt();
            if (OptionUtils.shouldSkip(messageOpts.getTagList(), this.tags)) {
                continue;
            }
            hasContent = true;
            generateMessage(descriptor, messageOpts);
            level(() -> generateNested(descriptor));
        }
        return hasContent;
    }

    private void generateNested(Descriptor descriptor) {
        Set<String> mapFields = descriptor.getFields().stream()
            .filter(FieldDescriptor::isMapField)
            .map(fieldDescriptor -> StringUtils.capitalize(fieldDescriptor.getName()) + "Entry")
            .collect(Collectors.toSet());
        List<Descriptor> nestedDescriptors = descriptor.getNestedTypes().stream()
            .filter(d -> !mapFields.contains(d.getName()))
            .collect(Collectors.toList());
        handleDescriptors(nestedDescriptors);
    }

    private void generateEnum(EnumDescriptor descriptor) {
        indent().append("export enum ")
            .append(descriptor.getName())
            .append(" {\n");

        for (EnumValueDescriptor valueDescriptor : descriptor.getValues()) {
            level(() -> indent().append(valueDescriptor.getName())
                .append(" = ").append(valueDescriptor.getIndex()).append(",\n")
            );
        }
        closeBracket();
    }

    private void generateMessage(Descriptor descriptor, OptMessageOpts messageOpts) {
        String className = descriptor.getName();

        indent().append("export interface ").append(interfaceName(className)).append(" {\n");
        level(() -> generateMessageFields(descriptor, true));
        closeBracket();

        indent()
            .append("export class ").append(className)
            .append(" implements ").append(interfaceName(className));
        if (StringUtils.isNotEmpty(messageOpts.getPath())) {
            builder.append(", Webpb.WebpbMessage {\n");
        } else {
            builder.append(" {\n");
        }

        level(() -> {
            generateMessageFields(descriptor, false);
            indent().append("webpbMeta: () => Webpb.WebpbMeta;\n\n");
            generateConstructor(descriptor, messageOpts, className);
        });
        closeBracket();
    }

    private void generateMessageFields(Descriptor descriptor, boolean isInterface) {
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            addImport(DescriptorUtils.getFieldTypeFilePackage(fieldDescriptor));
            indent().append(fieldDescriptor.getName());
            if (fieldDescriptor.isOptional()) {
                builder.append('?');
            } else if (!isInterface && !fieldDescriptor.hasDefaultValue()) {
                builder.append('!');
            }
            if (fieldDescriptor.isMapField()) {
                List<FieldDescriptor> fieldDescriptors =
                    fieldDescriptor.getMessageType().getFields();
                FieldDescriptor valueDescriptor = fieldDescriptors.get(1);
                builder.append(": ").append("{ [k: string]: ")
                    .append(getTypeName(valueDescriptor)).append(" }");
            } else {
                String typeName = getTypeName(fieldDescriptor);
                builder.append(": ").append(typeName);
                if (fieldDescriptor.isRepeated()) {
                    builder.append("[]");
                }
                if (!isInterface && fieldDescriptor.hasDefaultValue()) {
                    builder.append(" = ");
                    if ("string".equals(typeName)) {
                        builder.append('"').append(fieldDescriptor.getDefaultValue()).append('"');
                    } else {
                        builder.append(fieldDescriptor.getDefaultValue());
                    }
                }
            }
            builder.append(";\n");
        }
    }

    private void generateConstructor(Descriptor descriptor, OptMessageOpts messageOpts,
                                     String className) {
        if (descriptor.getFields().isEmpty()) {
            indent().append("private constructor() {\n");
            level(() -> {
                indent().append("this.webpbMeta = () => ({\n");
                initializeMeta(descriptor, messageOpts);
            });
            closeBracket();
            indent().append("static create(): ").append(className).append(" {\n");
            level(() -> indent().append("return new ").append(className).append("();\n"));
        } else {
            String interfaceName = interfaceName(className);
            indent().append("private constructor(p?: ").append(interfaceName).append(") {\n");
            level(() -> {
                indent().append("Webpb.assign(p, this, ")
                    .append(generateOmitted(descriptor)).append(");\n");
                indent().append("this.webpbMeta = () => (p && {\n");
                initializeMeta(descriptor, messageOpts);
            });
            closeBracket();
            indent().append("static create(properties: ")
                .append(interfaceName).append("): ").append(className).append(" {\n");
            level(() -> indent().append("return new ")
                .append(descriptor.getName())
                .append("(properties").append(");\n"));
        }
        closeBracket();
    }

    private void initializeMeta(Descriptor descriptor, OptMessageOpts messageOpts) {
        level(() -> {
            generateMetaField("class", "'" + descriptor.getName() + "'");
            String method = messageOpts.getMethod();
            generateMetaField("method", StringUtils.isEmpty(method) ? "''" : "'" + method + "'");
            String context = Utils.normalize(messageOpts.getContext());
            generateMetaField("context", StringUtils.isEmpty(context) ? "''" : "'" + context + "'");
            generateMetaPath(descriptor, Utils.normalize(messageOpts.getPath()));
        });
        trimDuplicatedNewline();
        indent().append("}) as Webpb.WebpbMeta;\n\n");
    }

    private void generateMetaPath(Descriptor descriptor, String path) {
        indent().append("path").append(": ");
        if (StringUtils.isEmpty(path)) {
            builder.append("''\n");
            return;
        }

        builder.append('`');
        ParamGroup group = ParamGroup.of(path);
        DescriptorUtils.validation(group, descriptor);
        Iterator<PathParam> iterator = group.getParams().iterator();
        while (iterator.hasNext()) {
            PathParam param = iterator.next();
            builder.append(param.getPrefix());
            if (StringUtils.isNotEmpty(param.getKey())) {
                if (builder.charAt(builder.length() - 1) == '?') {
                    builder.deleteCharAt(builder.length() - 1);
                }
                builder.append("${Webpb.query({\n");
                level(() -> {
                    indent().append(param.getKey()).append(": ")
                        .append(getter(param.getAccessor())).append(",\n");
                    while (iterator.hasNext()) {
                        PathParam p = iterator.next();
                        indent().append(p.getKey()).append(": ")
                            .append(getter(p.getAccessor())).append(",\n");
                    }
                });
                indent().append("})}`\n").append(group.getSuffix());
                return;
            }
            builder.append("${").append(getter(param.getAccessor())).append("}");
        }
        builder.append(group.getSuffix()).append("`\n");
    }

    private String getter(String value) {
        StringBuilder builder = new StringBuilder();
        if (value.contains(".")) {
            builder.append("Webpb.getter(p, '").append(value).append("')");
        } else {
            builder.append("p.").append(value);
        }
        return builder.toString();
    }

    private String generateOmitted(Descriptor descriptor) {
        StringBuilder builder = new StringBuilder("[");
        String separator = null;
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            OptFieldOpts fieldOpts = getOpts(fieldDescriptor, FieldOpts::hasOpt).getOpt();
            if (fieldOpts.getInQuery() || fieldOpts.getOmitted()) {
                if (separator != null) {
                    builder.append(separator);
                }
                builder.append('"').append(fieldDescriptor.getName()).append('"');
                separator = ", ";
            }
        }
        return builder.append("]").toString();
    }

    private void generateMetaField(String key, String value) {
        indent().append(key).append(": ").append(value == null ? "" : value).append(",\n");
    }

    private String getTypeName(FieldDescriptor fieldDescriptor) {
        WebpbExtend.TsFileOpts webpbOpts = requestContext.getFileOpts().getTs();
        WebpbExtend.TsFileOpts fileOpts = getOpts(fileDescriptor, FileOpts::hasTs).getTs();
        TsFieldOpts fieldOpts = getOpts(fieldDescriptor, FieldOpts::hasTs).getTs();
        FieldDescriptor.Type type = fieldDescriptor.getType();
        FieldDescriptor.JavaType javaType = fieldDescriptor.getJavaType();
        if (javaType == LONG) {
            if (webpbOpts.getInt64AsString() || fileOpts.getInt64AsString()) {
                return "string";
            } else if (fieldOpts.getAsString()) {
                return "string";
            }
        }
        if (TYPES.containsKey(type)) {
            return TYPES.get(type);
        }

        String packageName = fileDescriptor.getPackage();
        String fullName = DescriptorUtils.getFieldTypeFullName(fieldDescriptor);
        String simpleName = DescriptorUtils.getFieldTypeSimpleName(fieldDescriptor);
        if (StringUtils.startsWith(fullName, packageName)) {
            return DescriptorUtils.isMessage(fieldDescriptor) ? "I" + simpleName : simpleName;
        }
        return DescriptorUtils.isMessage(fieldDescriptor)
            ? DescriptorUtils.getFieldTypePackage(fieldDescriptor) + ".I" + simpleName : fullName;
    }

    private void trimDuplicatedNewline() {
        while (builder.length() > 1) {
            if (builder.charAt(builder.length() - 1) != '\n') {
                break;
            }
            if (builder.charAt(builder.length() - 2) != '\n') {
                break;
            }
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private void closeBracket() {
        trimDuplicatedNewline();
        indent().append("}\n\n");
    }

    private void level(Runnable runnable) {
        this.level++;
        runnable.run();
        this.level--;
    }

    private StringBuilder indent() {
        for (int i = 0; i < level; i++) {
            builder.append(INDENT);
        }
        return builder;
    }

    private String interfaceName(String className) {
        return "I" + className;
    }

    private void addImport(String typeOrPackage) {
        if (StringUtils.isNotEmpty(typeOrPackage)
            && !StringUtils.startsWith(typeOrPackage, fileDescriptor.getPackage())) {
            imports.add(typeOrPackage);
        }
    }
}
