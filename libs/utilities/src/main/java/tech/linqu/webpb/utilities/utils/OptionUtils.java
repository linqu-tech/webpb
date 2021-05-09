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

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.UnknownFieldSet;

import java.util.List;
import java.util.function.Predicate;

import static com.google.protobuf.UnknownFieldSet.Field;

public class OptionUtils {

    public static boolean shouldSkip(List<String> optionTags, List<String> tags) {
        if (tags.isEmpty()) {
            return false;
        }
        for (String tag : tags) {
            if (optionTags.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    public static WebpbExtend.FileOpts getOpts(FileDescriptor fileDescriptor, Predicate<WebpbExtend.FileOpts> predicate) {
        if (fileDescriptor == null) {
            return WebpbExtend.FileOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = fileDescriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                WebpbExtend.FileOpts opts;
                try {
                    opts = WebpbExtend.FileOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return WebpbExtend.FileOpts.getDefaultInstance();
    }

    public static WebpbExtend.MessageOpts getOpts(Descriptor descriptor, Predicate<WebpbExtend.MessageOpts> predicate) {
        if (descriptor == null) {
            return WebpbExtend.MessageOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                WebpbExtend.MessageOpts opts;
                try {
                    opts = WebpbExtend.MessageOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return WebpbExtend.MessageOpts.getDefaultInstance();
    }

    public static WebpbExtend.EnumOpts getOpts(EnumDescriptor descriptor, Predicate<WebpbExtend.EnumOpts> predicate) {
        if (descriptor == null) {
            return WebpbExtend.EnumOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                WebpbExtend.EnumOpts opts;
                try {
                    opts = WebpbExtend.EnumOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return WebpbExtend.EnumOpts.getDefaultInstance();
    }

    public static WebpbExtend.FieldOpts getOpts(FieldDescriptor descriptor, Predicate<WebpbExtend.FieldOpts> predicate) {
        if (descriptor == null) {
            return WebpbExtend.FieldOpts.getDefaultInstance();
        }
        UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
        for (Field field : fieldSet.asMap().values()) {
            for (ByteString byteString : field.getLengthDelimitedList()) {
                WebpbExtend.FieldOpts opts;
                try {
                    opts = WebpbExtend.FieldOpts.parseFrom(byteString);
                } catch (InvalidProtocolBufferException e) {
                    continue;
                }
                if (predicate.test(opts)) {
                    return opts;
                }
            }
        }
        return WebpbExtend.FieldOpts.getDefaultInstance();
    }
}
