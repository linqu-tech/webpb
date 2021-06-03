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

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE;

import com.google.protobuf.Descriptors.Descriptor;
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

    /**
     * Filed type is enum.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return is enum
     */
    public static boolean isEnum(FieldDescriptor fieldDescriptor) {
        return fieldDescriptor.getJavaType() == FieldDescriptor.JavaType.ENUM;
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
     * Resolve file package from type of the field.
     *
     * @param fieldDescriptor {@link FileDescriptor}.
     * @return file package name
     */
    public static String getFieldTypeFilePackage(FieldDescriptor fieldDescriptor) {
        if (isMessage(fieldDescriptor)) {
            return fieldDescriptor.getMessageType().getFile().getPackage();
        } else if (isEnum(fieldDescriptor)) {
            return fieldDescriptor.getEnumType().getFile().getPackage();
        } else {
            return null;
        }
    }

    /**
     * Resolve package from type of the field.
     *
     * @param fieldDescriptor {@link FileDescriptor}.
     * @return package name
     */
    public static String getFieldTypePackage(FieldDescriptor fieldDescriptor) {
        String fullName = getFieldTypeFullName(fieldDescriptor);
        if (StringUtils.isNotEmpty(fullName)) {
            String simpleName = getFieldTypeSimpleName(fieldDescriptor);
            return StringUtils.removeEnd(fullName, "." + simpleName);
        }
        return null;
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
            return null;
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
            return null;
        }
    }

    /**
     * Get key descriptor of a map field.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return {@link FileDescriptor}
     */
    public static FieldDescriptor getKeyDescriptor(FieldDescriptor fieldDescriptor) {
        List<FieldDescriptor> fieldDescriptors = fieldDescriptor.getMessageType().getFields();
        return fieldDescriptors.get(0);
    }

    /**
     * Get value descriptor of a map field.
     *
     * @param fieldDescriptor {@link FieldDescriptor}
     * @return {@link FileDescriptor}
     */
    public static FieldDescriptor getValueDescriptor(FieldDescriptor fieldDescriptor) {
        List<FieldDescriptor> fieldDescriptors = fieldDescriptor.getMessageType().getFields();
        return fieldDescriptors.get(1);
    }

    /**
     * Resolve a file descriptor by name recursively.
     *
     * @param descriptors from descriptors
     * @param dependency  dependency name
     * @return {@link FileDescriptor} or null
     */
    public static FileDescriptor resolveDescriptor(List<FileDescriptor> descriptors,
                                                   String dependency) {
        for (FileDescriptor descriptor : descriptors) {
            if (StringUtils.equalsIgnoreCase(descriptor.getName(), dependency)) {
                return descriptor;
            }
            FileDescriptor fileDescriptor =
                resolveDescriptor(descriptor.getDependencies(), dependency);
            if (fileDescriptor != null) {
                return fileDescriptor;
            }
        }
        return null;
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
