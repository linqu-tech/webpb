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

import static com.google.protobuf.UnknownFieldSet.Field;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.UnknownFieldSet;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumValueOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.MessageOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptEnumValueOpts;

/**
 * Utilities to handle options.
 */
public class OptionUtils {

    private OptionUtils() {
    }

    /**
     * Resolve {@link FieldOpts} from {@link FileDescriptor} with a filter.
     *
     * @param fileDescriptor {@link FileDescriptor}
     * @param predicate      option filter
     * @return {@link FieldOpts}
     */
    public static FileOpts getOpts(FileDescriptor fileDescriptor, Predicate<FileOpts> predicate) {
        if (fileDescriptor == null) {
            return FileOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = fileDescriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                FileOpts opts;
                try {
                    opts = FileOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return FileOpts.getDefaultInstance();
    }

    /**
     * Resolve {@link MessageOpts} from {@link FileDescriptor} with a filter.
     *
     * @param descriptor {@link Descriptor}
     * @param predicate  option filter
     * @return {@link MessageOpts}
     */
    public static MessageOpts getOpts(Descriptor descriptor, Predicate<MessageOpts> predicate) {
        if (descriptor == null) {
            return MessageOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                MessageOpts opts;
                try {
                    opts = MessageOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return MessageOpts.getDefaultInstance();
    }

    /**
     * Resolve {@link EnumOpts} from {@link FileDescriptor} with a filter.
     *
     * @param descriptor {@link EnumDescriptor}
     * @param predicate  option filter
     * @return {@link EnumOpts}
     */
    public static EnumOpts getOpts(EnumDescriptor descriptor, Predicate<EnumOpts> predicate) {
        if (descriptor == null) {
            return EnumOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                EnumOpts opts;
                try {
                    opts = EnumOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return EnumOpts.getDefaultInstance();
    }

    /**
     * Resolve {@link FieldOpts} from {@link FileDescriptor} with a filter.
     *
     * @param descriptor {@link FileDescriptor}
     * @param predicate  option filter
     * @return {@link FieldOpts}
     */
    public static FieldOpts getOpts(FieldDescriptor descriptor, Predicate<FieldOpts> predicate) {
        if (descriptor == null) {
            return FieldOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                FieldOpts opts;
                try {
                    opts = FieldOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return FieldOpts.getDefaultInstance();
    }

    /**
     * Resolve {@link EnumValueOpts} from {@link EnumValueDescriptor} with a filter.
     *
     * @param descriptor {@link EnumValueDescriptor}
     * @param predicate  option filter
     * @return {@link EnumValueOpts}
     */
    public static EnumValueOpts getOpts(EnumValueDescriptor descriptor,
                                        Predicate<EnumValueOpts> predicate) {
        if (descriptor == null) {
            return EnumValueOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                EnumValueOpts opts;
                try {
                    opts = EnumValueOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return EnumValueOpts.getDefaultInstance();
    }

    /**
     * If this enum use string value.
     *
     * @param descriptor {@link EnumDescriptor}
     * @return true if use string value
     */
    public static boolean isStringValue(EnumDescriptor descriptor) {
        EnumOpts enumOpts = getOpts(descriptor, EnumOpts::hasOpt);
        if (enumOpts.getOpt().getStringValue()) {
            return true;
        }
        for (Descriptors.EnumValueDescriptor valueDescriptor : descriptor.getValues()) {
            OptEnumValueOpts opts =
                OptionUtils.getOpts(valueDescriptor, EnumValueOpts::hasOpt).getOpt();
            if (!StringUtils.isEmpty(opts.getValue())) {
                return true;
            }
        }
        return false;
    }
}
