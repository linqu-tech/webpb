package tech.linqu.webpb.utilities.utils;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tech.linqu.webpb.utilities.test.TestUtils.createRequest;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getFieldTypeFullName;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getFieldTypePackage;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getFieldTypeSimpleName;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getMapKeyDescriptor;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getMapValueDescriptor;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveEnum;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveFile;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveMessage;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.validation;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.commons.SegmentGroup;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;

class DescriptorUtilsTest {

    @Test
    void shouldCheckIsEnumSuccess() {
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        when(fieldDescriptor.getJavaType()).thenReturn(FieldDescriptor.JavaType.ENUM);
        assertTrue(DescriptorUtils.isEnum(fieldDescriptor));

        when(fieldDescriptor.getJavaType()).thenReturn(FieldDescriptor.JavaType.MESSAGE);
        assertFalse(DescriptorUtils.isEnum(fieldDescriptor));
    }

    @Test
    void shouldCheckIsMessageSuccess() {
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        when(fieldDescriptor.getJavaType()).thenReturn(FieldDescriptor.JavaType.MESSAGE);
        assertTrue(DescriptorUtils.isMessage(fieldDescriptor));

        when(fieldDescriptor.getJavaType()).thenReturn(FieldDescriptor.JavaType.ENUM);
        assertFalse(DescriptorUtils.isMessage(fieldDescriptor));
    }

    @Test
    void shouldResolveDescriptorSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        assertNotNull(resolveMessage(context.getDescriptors(), "Test"));
        assertNull(resolveMessage(context.getDescriptors(), "NotExists"));
    }

    @Test
    void shouldResolveFileDescriptorSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        FileDescriptor descriptor = resolveFile(context.getDescriptors(), "Test.proto");
        assertNotNull(descriptor);
        assertNotNull(resolveFile(singletonList(descriptor), "Include.proto"));
        assertNull(resolveFile(context.getDescriptors(), "NotExists"));
    }

    @Test
    void shouldResolveEnumDescriptorSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        assertNotNull(resolveEnum(context.getDescriptors(), "Enum"));
        assertNull(resolveEnum(context.getDescriptors(), "NotExists"));
    }

    @Test
    void shouldGetFieldTypeFilePackageSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        assertNull(getFieldTypePackage(descriptor.getFields().get(0)));
        assertEquals("IncludeProto", getFieldTypePackage(descriptor.getFields().get(1)));
        assertEquals("IncludeProto", getFieldTypePackage(descriptor.getFields().get(2)));
    }

    @Test
    void shouldGetFieldTypeSimpleNameSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        assertEquals("INT32", getFieldTypeSimpleName(descriptor.getFields().get(0)));
        assertEquals("Message", getFieldTypeSimpleName(descriptor.getFields().get(1)));
        assertEquals("Enum", getFieldTypeSimpleName(descriptor.getFields().get(2)));
    }

    @Test
    void shouldGetFieldTypePackageSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        assertNull(getFieldTypePackage(descriptor.getFields().get(0)));
        assertEquals("IncludeProto", getFieldTypePackage(descriptor.getFields().get(1)));
        assertEquals("IncludeProto", getFieldTypePackage(descriptor.getFields().get(2)));
    }

    @Test
    void shouldGetFieldTypeFullNameSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        assertEquals("INT32", getFieldTypeFullName(descriptor.getFields().get(0)));
        assertEquals("IncludeProto.Message", getFieldTypeFullName(descriptor.getFields().get(1)));
        assertEquals("IncludeProto.Enum", getFieldTypeFullName(descriptor.getFields().get(2)));
    }

    @Test
    void shouldGetMapKeyDescriptorSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        assertNotNull(getMapKeyDescriptor(descriptor.getFields().get(4)));
    }

    @Test
    void shouldGetMapValueDescriptorSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        assertNotNull(descriptor);
        assertNotNull(getMapValueDescriptor(descriptor.getFields().get(4)));
    }

    @Test
    void shouldValidationSuccess() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        SegmentGroup group = SegmentGroup.of("/{test1}/{test2.id}");
        assertDoesNotThrow(() -> validation(group, descriptor));
    }

    @Test
    void shouldValidationThrowException() {
        RequestContext context = createRequest(Dumps.TEST1);
        Descriptor descriptor = resolveMessage(context.getDescriptors(), "Test");
        SegmentGroup group = SegmentGroup.of("/{test1}/{test2.id}?value={notExists}");
        assertThrows(RuntimeException.class, () -> validation(group, descriptor),
            "Invalid accessor notExists");
    }
}
