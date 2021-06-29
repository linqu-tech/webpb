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

package tech.linqu.webpb.utilities.utils;

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.ENUM;
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.commons.ParamGroup;
import tech.linqu.webpb.commons.PathParam;

/**
 * Utilities to handle protobuf descriptors.
 */
public class DescriptorUtils {

    private DescriptorUtils() {
    }

    /**
     * Filed type is enum.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return is enum
     */
    public static boolean isEnum(FieldDescriptor fieldDescriptor) {
        return fieldDescriptor.getJavaType() == ENUM;
    }

    /**
     * Field type is message.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return is message
     */
    public static boolean isMessage(FieldDescriptor fieldDescriptor) {
        return fieldDescriptor.getJavaType() == MESSAGE;
    }

    /**
     * Resolve a message descriptor by name recursively.
     *
     * @param descriptors from descriptors
     * @param name        descriptor name
     * @return {@link Descriptor} or null
     */
    public static Descriptor resolveMessage(List<FileDescriptor> descriptors, String name) {
        for (FileDescriptor fileDescriptor : descriptors) {
            for (Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                if (StringUtils.equalsIgnoreCase(name, descriptor.getName())) {
                    return descriptor;
                }
            }
        }
        return null;
    }

    /**
     * Resolve an enum descriptor by name recursively.
     *
     * @param descriptors from descriptors
     * @param name        descriptor name
     * @return {@link Descriptor} or null
     */
    public static EnumDescriptor resolveEnum(List<FileDescriptor> descriptors, String name) {
        for (FileDescriptor fileDescriptor : descriptors) {
            for (EnumDescriptor descriptor : fileDescriptor.getEnumTypes()) {
                if (StringUtils.equalsIgnoreCase(name, descriptor.getName())) {
                    return descriptor;
                }
            }
        }
        return null;
    }

    /**
     * Resolve a file descriptor by name recursively.
     *
     * @param descriptors from descriptors
     * @param regex       descriptor name regex
     * @return {@link FileDescriptor} or null
     */
    public static FileDescriptor resolveFile(List<FileDescriptor> descriptors, String regex) {
        for (FileDescriptor descriptor : descriptors) {
            if (descriptor.getName().matches(regex)) {
                return descriptor;
            }
            FileDescriptor fileDescriptor = resolveFile(descriptor.getDependencies(), regex);
            if (fileDescriptor != null) {
                return fileDescriptor;
            }
        }
        return null;
    }

    /**
     * Resolve file package from type of the field.
     *
     * @param fieldDescriptor {@link FileDescriptor}.
     * @return file package name
     */
    public static String getFieldTypePackage(FieldDescriptor fieldDescriptor) {
        if (isMessage(fieldDescriptor)) {
            return fieldDescriptor.getMessageType().getFile().getPackage();
        } else if (isEnum(fieldDescriptor)) {
            return fieldDescriptor.getEnumType().getFile().getPackage();
        } else {
            return null;
        }
    }

    /**
     * Resolve simple name from type of the field.
     *
     * @param fieldDescriptor {@link FileDescriptor}.
     * @return simple type name
     */
    public static String getFieldTypeSimpleName(FieldDescriptor fieldDescriptor) {
        if (isMessage(fieldDescriptor)) {
            return fieldDescriptor.getMessageType().getName();
        } else if (isEnum(fieldDescriptor)) {
            return fieldDescriptor.getEnumType().getName();
        } else {
            return fieldDescriptor.getType().name();
        }
    }

    /**
     * Resolve full name from type of the field.
     *
     * @param fieldDescriptor {@link FileDescriptor}.
     * @return full type name
     */
    public static String getFieldTypeFullName(FieldDescriptor fieldDescriptor) {
        if (isMessage(fieldDescriptor)) {
            return fieldDescriptor.getMessageType().getFullName();
        } else if (isEnum(fieldDescriptor)) {
            return fieldDescriptor.getEnumType().getFullName();
        } else {
            return fieldDescriptor.getType().name();
        }
    }

    /**
     * Get key descriptor of a map field.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return {@link FileDescriptor}
     */
    public static FieldDescriptor getMapKeyDescriptor(FieldDescriptor fieldDescriptor) {
        List<FieldDescriptor> fieldDescriptors = fieldDescriptor.getMessageType().getFields();
        return fieldDescriptors.get(0);
    }

    /**
     * Get value descriptor of a map field.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return {@link FileDescriptor}
     */
    public static FieldDescriptor getMapValueDescriptor(FieldDescriptor fieldDescriptor) {
        List<FieldDescriptor> fieldDescriptors = fieldDescriptor.getMessageType().getFields();
        return fieldDescriptors.get(1);
    }

    /**
     * Validate the descriptor contains required path variables.
     *
     * @param group      {@link ParamGroup}
     * @param descriptor {@link Descriptor}
     */
    public static void validation(ParamGroup group, Descriptor descriptor) {
        for (PathParam param : group.getParams()) {
            if (!validate(param.getAccessor(), descriptor)) {
                throw new RuntimeException("Invalid accessor " + param.getAccessor());
            }
        }
    }

    private static boolean validate(String accessor, Descriptor descriptor) {
        for (String name : accessor.split("\\.")) {
            FieldDescriptor fieldDescriptor = descriptor.findFieldByName(name);
            if (fieldDescriptor == null) {
                return false;
            }
            if (fieldDescriptor.getJavaType() == MESSAGE) {
                descriptor = fieldDescriptor.getMessageType();
            }
        }
        return true;
    }
}
