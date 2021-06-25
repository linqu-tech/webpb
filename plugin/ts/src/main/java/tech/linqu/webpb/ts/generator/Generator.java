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
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getFieldTypeFullName;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getFieldTypePackage;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.isMessage;
import static tech.linqu.webpb.utilities.utils.OptionUtils.getOpts;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.commons.ParamGroup;
import tech.linqu.webpb.commons.PathParam;
import tech.linqu.webpb.ts.utils.Imports;
import tech.linqu.webpb.ts.utils.SourceBuilder;
import tech.linqu.webpb.ts.utils.TsUtils;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumValueOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.MessageOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptEnumValueOpts;
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

    private final SourceBuilder builder = new SourceBuilder();

    private Imports imports;

    private RequestContext requestContext;

    private FileDescriptor fileDescriptor;

    public static Generator create() {
        return new Generator();
    }

    /**
     * Entrance of the generator.
     *
     * @return {@link SourceBuilder}
     */
    public String generate(RequestContext requestContext, FileDescriptor fileDescriptor) {
        if (shouldIgnore(fileDescriptor.getPackage())) {
            return null;
        }
        this.requestContext = requestContext;
        this.fileDescriptor = fileDescriptor;
        this.imports = new Imports(fileDescriptor.getPackage());
        generateTypes();
        StringBuilder builder = new StringBuilder();
        builder.append("// " + Const.HEADER + "\n");
        builder.append("// " + Const.GIT_URL + "\n\n");
        builder.append("import * as Webpb from 'webpb';\n\n");
        imports.updateBuilder(builder);
        this.builder.prepend(builder.toString());
        this.builder.alignNewline(1);
        return this.builder.toString();
    }

    private static boolean shouldIgnore(String packageName) {
        return StringUtils.isEmpty(packageName) || packageName.contains("google.protobuf");
    }

    private void generateTypes() {
        builder.append("export namespace ").append(fileDescriptor.getPackage()).append(" {\n");
        builder.level(() -> {
            for (EnumDescriptor enumDescriptor : fileDescriptor.getEnumTypes()) {
                generateEnum(enumDescriptor);
            }
            handleDescriptors(fileDescriptor.getMessageTypes());
            builder.trimDuplicatedNewline();
        });
        builder.closeBracket();
    }

    private void handleDescriptors(List<Descriptor> descriptors) {
        for (Descriptor descriptor : descriptors) {
            OptMessageOpts messageOpts = getOpts(descriptor, MessageOpts::hasOpt).getOpt();
            generateMessage(descriptor, messageOpts);
            generateNested(descriptor);
        }
    }

    private void generateNested(Descriptor descriptor) {
        String namespace = descriptor.getName();
        Set<String> mapFields = descriptor.getFields().stream()
            .filter(FieldDescriptor::isMapField)
            .map(fieldDescriptor -> StringUtils.capitalize(fieldDescriptor.getName()) + "Entry")
            .collect(Collectors.toSet());
        List<Descriptor> nestedDescriptors = descriptor.getNestedTypes().stream()
            .filter(d -> !mapFields.contains(d.getName()))
            .collect(Collectors.toList());
        if (!nestedDescriptors.isEmpty()) {
            builder.indent().append("export namespace ").append(namespace).append(" {\n");
            builder.level(() -> handleDescriptors(nestedDescriptors));
            builder.closeBracket();
        }
    }

    private void generateEnum(EnumDescriptor descriptor) {
        builder.indent().append("export enum ")
            .append(descriptor.getName())
            .append(" {\n");

        boolean stringValue = OptionUtils.isStringValue(descriptor);
        for (EnumValueDescriptor valueDescriptor : descriptor.getValues()) {
            OptEnumValueOpts opts =
                OptionUtils.getOpts(valueDescriptor, EnumValueOpts::hasOpt).getOpt();
            builder.level(() -> {
                builder.indent().append(valueDescriptor.getName()).append(" = ");
                if (stringValue) {
                    builder.append("'");
                    if (StringUtils.isEmpty(opts.getValue())) {
                        builder.append(valueDescriptor.getName());
                    } else {
                        builder.append(opts.getValue());
                    }
                    builder.append("'");
                } else {
                    builder.append(valueDescriptor.getIndex());
                }
                builder.append(",\n");
            });
        }
        builder.closeBracket();
    }

    private void generateMessage(Descriptor descriptor, OptMessageOpts messageOpts) {
        String className = descriptor.getName();

        builder.indent()
            .append("export interface ").append(toInterfaceName(className)).append(" {\n");
        builder.level(() -> generateMessageFields(descriptor, true));
        builder.closeBracket();

        builder.indent()
            .append("export class ").append(className)
            .append(" implements ").append(toInterfaceName(className));
        if (StringUtils.isNotEmpty(messageOpts.getPath())) {
            builder.append(", Webpb.WebpbMessage {\n");
        } else {
            builder.append(" {\n");
        }

        builder.level(() -> {
            generateMessageFields(descriptor, false);
            builder.indent().append("webpbMeta: () => Webpb.WebpbMeta;\n");
            generateToWebpbAlias(descriptor);
            generateConstructor(descriptor, messageOpts, className);
        });
        builder.closeBracket();
    }

    private void generateToWebpbAlias(Descriptor descriptor) {
        if (!TsUtils.toAlias(descriptor)) {
            builder.indent().append("toWebpbAlias = () => this;").alignNewline(2);
            return;
        }
        builder.indent().append("toWebpbAlias = () => Webpb.toAlias(this, {");
        boolean generated = builder.level(() -> {
            boolean v = false;
            for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
                String alias = TsUtils.getAlias(fieldDescriptor);
                if (StringUtils.isEmpty(alias)) {
                    continue;
                }
                builder.append("\n").indent().append(fieldDescriptor.getName())
                    .append(": '").append(alias).append("'");
                v = true;
            }
            return v;
        });
        if (generated) {
            builder.append('\n').indent().append("});").alignNewline(2);
        } else {
            builder.append("});").alignNewline(2);
        }
    }

    private void generateMessageFields(Descriptor descriptor, boolean isInterface) {
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            builder.indent().append(fieldDescriptor.getName());
            if (fieldDescriptor.isOptional()) {
                builder.append('?');
            } else if (!isInterface && !fieldDescriptor.hasDefaultValue()) {
                builder.append('!');
            }
            if (fieldDescriptor.isMapField()) {
                List<FieldDescriptor> descriptors = fieldDescriptor.getMessageType().getFields();
                String typeName = getTypeAndImport(descriptors.get(1));
                builder.append(": ").append("{ [k: string]: ").append(typeName).append(" }");
            } else {
                String typeName = getTypeAndImport(fieldDescriptor);
                builder.append(": ").append(typeName);
                if (fieldDescriptor.isRepeated()) {
                    builder.append("[]");
                }
                if (!isInterface && fieldDescriptor.hasDefaultValue()) {
                    builder.append(" = ");
                    if ("string".equals(typeName)) {
                        builder.append('"')
                            .appendObj(fieldDescriptor.getDefaultValue()).append('"');
                    } else {
                        builder.appendObj(fieldDescriptor.getDefaultValue());
                    }
                }
            }
            builder.append(";\n");
        }
    }

    private void generateConstructor(Descriptor descriptor, OptMessageOpts messageOpts,
                                     String className) {
        if (descriptor.getFields().isEmpty()) {
            builder.indent().append("private constructor() {\n");
            builder.level(() -> {
                builder.indent().append("this.webpbMeta = () => ({\n");
                initializeMeta(descriptor, messageOpts);
            });
            builder.closeBracket();
            builder.indent().append("static create(): ").append(className).append(" {\n");
            builder.level(() ->
                builder.indent().append("return new ").append(className).append("();\n"));
        } else {
            String interfaceName = toInterfaceName(className);
            builder.indent().append("private constructor(p?: ").append(interfaceName)
                .append(") {\n");
            builder.level(() -> {
                builder.indent().append("Webpb.assign(p, this, ")
                    .append(generateOmitted(descriptor)).append(");\n");
                builder.indent().append("this.webpbMeta = () => (p && {\n");
                initializeMeta(descriptor, messageOpts);
            });
            builder.closeBracket();
            builder.indent().append("static create(properties: ")
                .append(interfaceName).append("): ").append(className).append(" {\n");
            builder.level(() -> builder.indent().append("return new ")
                .append(descriptor.getName())
                .append("(properties").append(");\n"));
        }
        builder.closeBracket();
    }

    private void initializeMeta(Descriptor descriptor, OptMessageOpts messageOpts) {
        builder.level(() -> {
            generateMetaField("class", descriptor.getName());
            String method = messageOpts.getMethod();
            generateMetaField("method", method);
            String context = Utils.normalize(messageOpts.getContext());
            generateMetaField("context", context);
            generateMetaPath(descriptor, Utils.normalize(messageOpts.getPath()));
        });
        builder.trimDuplicatedNewline();
        builder.indent().append("}) as Webpb.WebpbMeta;").alignNewline(2);
    }

    private void generateMetaField(String key, String value) {
        builder.indent().append(key).append(": ")
            .append(StringUtils.isEmpty(value) ? "''" : "'" + value + "'")
            .append(",\n");
    }

    private void generateMetaPath(Descriptor descriptor, String path) {
        builder.indent().append("path").append(": ");
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
            String prefix = StringUtils.removeEnd(param.getPrefix(), "?");
            builder.append(prefix);
            String pre = StringUtils.contains(prefix, "?") ? "'&'" : "'?'";
            if (StringUtils.isNotEmpty(param.getKey())) {
                do {
                    builder.append("${Webpb.query(").append(pre).append(", {\n");
                    PathParam paramFinal = param;
                    builder.level(() -> builder.indent()
                        .append(paramFinal.getKey()).append(": ")
                        .append(getter(paramFinal.getAccessor())).append("\n")
                    );
                    builder.indent().append("})}");
                    if (!iterator.hasNext()) {
                        break;
                    }
                    param = iterator.next();
                    pre = "'&'";
                } while (true);
                if (StringUtils.isNotEmpty(group.getSuffix())) {
                    builder.append("&").append(group.getSuffix());
                }
                builder.append("`\n");
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

    private String getTypeAndImport(FieldDescriptor fieldDescriptor) {
        WebpbExtend.TsFileOpts webpbOpts = requestContext.getFileOpts().getTs();
        WebpbExtend.TsFileOpts fileOpts = getOpts(fileDescriptor, FileOpts::hasTs).getTs();
        TsFieldOpts fieldOpts = getOpts(fieldDescriptor, FieldOpts::hasTs).getTs();
        FieldDescriptor.Type type = fieldDescriptor.getType();
        FieldDescriptor.JavaType javaType = fieldDescriptor.getJavaType();
        if (javaType == LONG) {
            if (webpbOpts.getInt64AsString()) {
                return "string";
            } else if (fileOpts.getInt64AsString()) {
                return "string";
            } else if (fieldOpts.getAsString()) {
                return "string";
            }
        }
        if (TYPES.containsKey(type)) {
            return TYPES.get(type);
        }

        String fullName = getFieldTypeFullName(fieldDescriptor);
        if ("google.protobuf.Any".equals(fullName)) {
            return "unknown";
        }
        String name = isMessage(fieldDescriptor) ? toInterfaceName(fullName) : fullName;
        String packageName = fileDescriptor.getPackage();
        if (StringUtils.startsWith(name, packageName)) {
            return name.substring(packageName.length() + 1);
        }
        String fieldPackage = getFieldTypePackage(fieldDescriptor);
        imports.checkAndImport(fieldPackage);
        return name;
    }

    private String toInterfaceName(String name) {
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex < 0) {
            return "I" + name;
        }
        return name.substring(0, lastIndex) + ".I" + name.substring(lastIndex + 1);
    }
}
