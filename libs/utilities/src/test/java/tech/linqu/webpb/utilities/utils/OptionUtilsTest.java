package tech.linqu.webpb.utilities.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static tech.linqu.webpb.utilities.TestUtils.createRequest;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveDescriptor;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveEnumDescriptor;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveFileDescriptor;
import static tech.linqu.webpb.utilities.utils.OptionUtils.shouldSkip;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.MessageOpts;

class OptionUtilsTest {

    @Test
    void shouldTestSkipSuccess() {
        assertFalse(shouldSkip(Collections.emptyList(), Collections.emptyList()));
        assertFalse(shouldSkip(Arrays.asList("a", "b"), Arrays.asList("b", "c")));
        assertTrue(shouldSkip(Arrays.asList("a", "b"), Arrays.asList("c", "d")));
    }

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
        FileDescriptor descriptor = resolveFileDescriptor(context.getDescriptors(), "Test.proto");
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
        assertEquals(0, javaOpts.getJava().getAnnotationCount());
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
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Test");
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
        assertEquals("A", optOpts.getOpt().getTag(0));

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
        Descriptor descriptor = resolveDescriptor(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        FieldDescriptor fieldDescriptor = descriptor.getFields().get(1);
        assertEquals(FieldOpts.getDefaultInstance(),
            OptionUtils.getOpts((FieldDescriptor) null, o -> true));
        assertEquals(FieldOpts.getDefaultInstance(),
            OptionUtils.getOpts(fieldDescriptor, o -> true));
    }
}
