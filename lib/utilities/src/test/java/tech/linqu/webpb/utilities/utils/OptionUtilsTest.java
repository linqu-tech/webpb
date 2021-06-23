package tech.linqu.webpb.utilities.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static tech.linqu.webpb.utilities.test.TestUtils.createRequest;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveDescriptor;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveEnumDescriptor;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveFileDescriptor;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumValueOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.MessageOpts;

class OptionUtilsTest {

    // FileOpts
    @Test
    void shouldGetFileOptsSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        FileDescriptor descriptor = resolveFileDescriptor(context.getDescriptors(), "Test.proto");

        FileOpts javaOpts = OptionUtils.getOpts(descriptor, FileOpts::hasJava);
        assertTrue(javaOpts.getJava().getGenGetter());

        FileOpts tsOpts = OptionUtils.getOpts(descriptor, FileOpts::hasTs);
        assertTrue(tsOpts.getTs().getInt64AsString());
    }

    @Test
    void shouldGetFileOptsSuccessWhenParseError() {
        RequestContext context = createRequest(Dumps.TEST1);
        FileDescriptor descriptor = resolveFileDescriptor(context.getDescriptors(), "Test.proto");
        try (MockedStatic<FileOpts> opts = mockStatic(FileOpts.class)) {
            opts.when(() -> FileOpts.parseFrom((ByteString) any()))
                .thenThrow(new InvalidProtocolBufferException("Invalid"));
            assertEquals(FileOpts.getDefaultInstance(), OptionUtils.getOpts(descriptor, o -> true));
        }
    }

    @Test
    void shouldGetFileOptsSuccessWhenWithoutOptions() {
        RequestContext context = createRequest(Dumps.TEST2);
        FileDescriptor descriptor = resolveFileDescriptor(context.getDescriptors(), "Test1.proto");
        assertEquals(FileOpts.getDefaultInstance(),
            OptionUtils.getOpts((FileDescriptor) null, o -> true));
        assertEquals(FileOpts.getDefaultInstance(),
            OptionUtils.getOpts(descriptor, o -> true));
    }

    // MessageOpts
    @Test
    void shouldGetMessageOptsSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Test");

        MessageOpts optOpts = OptionUtils.getOpts(descriptor, MessageOpts::hasOpt);
        assertEquals("GET", optOpts.getOpt().getMethod());

        MessageOpts javaOpts = OptionUtils.getOpts(descriptor, MessageOpts::hasJava);
        assertEquals(2, javaOpts.getJava().getAnnotationCount());
    }

    @Test
    void shouldGetMessageOptsSuccessWhenParseError() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Test");
        try (MockedStatic<MessageOpts> opts = mockStatic(MessageOpts.class)) {
            opts.when(() -> MessageOpts.parseFrom((ByteString) any()))
                .thenThrow(new InvalidProtocolBufferException("Invalid"));
            assertEquals(MessageOpts.getDefaultInstance(),
                OptionUtils.getOpts(descriptor, o -> true));
        }
    }

    @Test
    void shouldGetMessageOptsSuccessWhenWithoutOptions() {
        RequestContext context = createRequest(Dumps.TEST2);
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Test1");
        assertEquals(MessageOpts.getDefaultInstance(),
            OptionUtils.getOpts((Descriptor) null, o -> true));
        assertEquals(MessageOpts.getDefaultInstance(),
            OptionUtils.getOpts(descriptor, o -> true));
    }

    // EnumOpts
    @Test
    void shouldGetEnumOptsSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        EnumDescriptor enumDescriptor = resolveEnumDescriptor(context.getDescriptors(), "Enum");

        EnumOpts optOpts = OptionUtils.getOpts(enumDescriptor, EnumOpts::hasOpt);
        assertNotNull(optOpts);

        EnumOpts javaOpts = OptionUtils.getOpts(enumDescriptor, EnumOpts::hasJava);
        assertEquals(0, javaOpts.getJava().getAnnotationCount());
    }

    @Test
    void shouldGetEnumOptsSuccessWhenParseError() {
        RequestContext context = createRequest(Dumps.TEST1);
        EnumDescriptor enumDescriptor = resolveEnumDescriptor(context.getDescriptors(), "Enum");
        try (MockedStatic<EnumOpts> opts = mockStatic(EnumOpts.class)) {
            opts.when(() -> EnumOpts.parseFrom((ByteString) any()))
                .thenThrow(new InvalidProtocolBufferException("Invalid"));
            assertEquals(EnumOpts.getDefaultInstance(),
                OptionUtils.getOpts(enumDescriptor, o -> true));
        }
    }

    @Test
    void shouldGetEnumOptsSuccessWhenWithoutOptions() {
        RequestContext context = createRequest(Dumps.TEST2);
        EnumDescriptor enumDescriptor = resolveEnumDescriptor(context.getDescriptors(), "Enum");
        assertEquals(EnumOpts.getDefaultInstance(),
            OptionUtils.getOpts((EnumDescriptor) null, o -> true));
        assertEquals(EnumOpts.getDefaultInstance(),
            OptionUtils.getOpts(enumDescriptor, o -> true));
    }

    // FieldOpts
    @Test
    void shouldGetFieldOptsSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        FieldDescriptor fieldDescriptor = descriptor.getFields().get(0);

        FieldOpts optOpts = OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasOpt);
        assertTrue(optOpts.getOpt().getInQuery());

        FieldOpts tsOpts = OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasTs);
        assertTrue(tsOpts.getTs().getAsString());
    }

    @Test
    void shouldGetFieldOptsSuccessWhenParseError() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        FieldDescriptor fieldDescriptor = descriptor.getFields().get(0);
        try (MockedStatic<FieldOpts> opts = mockStatic(FieldOpts.class)) {
            opts.when(() -> FieldOpts.parseFrom((ByteString) any()))
                .thenThrow(new InvalidProtocolBufferException("Invalid"));
            assertEquals(FieldOpts.getDefaultInstance(),
                OptionUtils.getOpts(fieldDescriptor, o -> true));
        }
    }

    @Test
    void shouldGetFieldOptsSuccessWhenWithoutOptions() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Data");
        assertNotNull(descriptor);
        FieldDescriptor fieldDescriptor = descriptor.getFields().get(0);
        assertEquals(FieldOpts.getDefaultInstance(),
            OptionUtils.getOpts((FieldDescriptor) null, o -> true));
        assertEquals(FieldOpts.getDefaultInstance(),
            OptionUtils.getOpts(fieldDescriptor, o -> true));
    }

    // EnumValueOpts
    @Test
    void shouldGetEnumValueOptsSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        EnumDescriptor descriptor = resolveEnumDescriptor(context.getDescriptors(), "Test5");
        assertNotNull(descriptor);
        EnumValueDescriptor enumValueDescriptor = descriptor.getValues().get(0);

        EnumValueOpts javaOpts = OptionUtils.getOpts(enumValueDescriptor, EnumValueOpts::hasJava);
        assertEquals(0, javaOpts.getJava().getAnnotationCount());

        EnumValueOpts tsOpts = OptionUtils.getOpts(enumValueDescriptor, EnumValueOpts::hasTs);
        assertEquals("text1", tsOpts.getTs().getValue());
    }

    @Test
    void shouldGetEnumValueOptsSuccessWhenParseError() {
        RequestContext context = createRequest(Dumps.TEST1);
        EnumDescriptor descriptor = resolveEnumDescriptor(context.getDescriptors(), "Test5");
        assertNotNull(descriptor);
        EnumValueDescriptor enumValueDescriptor = descriptor.getValues().get(0);
        try (MockedStatic<EnumValueOpts> opts = mockStatic(EnumValueOpts.class)) {
            opts.when(() -> EnumValueOpts.parseFrom((ByteString) any()))
                .thenThrow(new InvalidProtocolBufferException("Invalid"));
            assertEquals(EnumValueOpts.getDefaultInstance(),
                OptionUtils.getOpts(enumValueDescriptor, o -> true));
        }
    }

    @Test
    void shouldGetEnumValueOptsSuccessWhenWithoutOptions() {
        RequestContext context = createRequest(Dumps.TEST1);
        EnumDescriptor descriptor = resolveEnumDescriptor(context.getDescriptors(), "Test5");
        assertNotNull(descriptor);
        EnumValueDescriptor enumValueDescriptor = descriptor.getValues().get(2);
        assertEquals(EnumValueOpts.getDefaultInstance(),
            OptionUtils.getOpts((EnumValueDescriptor) null, o -> true));
        assertEquals(EnumValueOpts.getDefaultInstance(),
            OptionUtils.getOpts(enumValueDescriptor, o -> true));
    }
}
