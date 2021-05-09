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

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.commons.ParamGroup;
import tech.linqu.webpb.commons.PathParam;

import java.util.List;

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE;

public class DescriptorUtils {

    public static boolean isScalar(FieldDescriptor fieldDescriptor) {
        return !(isEnum(fieldDescriptor) || isMessage(fieldDescriptor));
    }

    public static boolean isEnum(FieldDescriptor fieldDescriptor) {
        return fieldDescriptor.getJavaType() == FieldDescriptor.JavaType.ENUM;
    }

    public static boolean isMessage(FieldDescriptor fieldDescriptor) {
        return fieldDescriptor.getJavaType() == MESSAGE;
    }

    public static String getFieldTypeFilePackage(FieldDescriptor fieldDescriptor) {
        if (isMessage(fieldDescriptor)) {
            return fieldDescriptor.getMessageType().getFile().getPackage();
        } else if (isEnum(fieldDescriptor)) {
            return fieldDescriptor.getEnumType().getFile().getPackage();
        } else {
            return null;
        }
    }

    public static String getFieldTypePackage(FieldDescriptor fieldDescriptor) {
        String fullName = getFieldTypeFullName(fieldDescriptor);
        if (StringUtils.isNotEmpty(fullName)) {
            String simpleName = getFieldTypeSimpleName(fieldDescriptor);
            return StringUtils.removeEnd(fullName, "." + simpleName);
        }
        return null;
    }

    public static String getFieldTypeSimpleName(FieldDescriptor fieldDescriptor) {
        if (isMessage(fieldDescriptor)) {
            return fieldDescriptor.getMessageType().getName();
        } else if (isEnum(fieldDescriptor)) {
            return fieldDescriptor.getEnumType().getName();
        } else {
            return null;
        }
    }

    public static String getFieldTypeFullName(FieldDescriptor fieldDescriptor) {
        if (isMessage(fieldDescriptor)) {
            return fieldDescriptor.getMessageType().getFullName();
        } else if (isEnum(fieldDescriptor)) {
            return fieldDescriptor.getEnumType().getFullName();
        } else {
            return null;
        }
    }

    public static FieldDescriptor getKeyDescriptor(FieldDescriptor fieldDescriptor) {
        List<FieldDescriptor> fieldDescriptors = fieldDescriptor.getMessageType().getFields();
        return fieldDescriptors.get(0);
    }

    public static FieldDescriptor getValueDescriptor(FieldDescriptor fieldDescriptor) {
        List<FieldDescriptor> fieldDescriptors = fieldDescriptor.getMessageType().getFields();
        return fieldDescriptors.get(1);
    }

    public static FileDescriptor resolveDescriptor(List<FileDescriptor> descriptors, String dependency) {
        for (FileDescriptor descriptor : descriptors) {
            if (StringUtils.equalsIgnoreCase(descriptor.getName(), dependency)) {
                return descriptor;
            }
            FileDescriptor fileDescriptor = resolveDescriptor(descriptor.getDependencies(), dependency);
            if (fileDescriptor != null) {
                return fileDescriptor;
            }
        }
        return null;
    }

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
