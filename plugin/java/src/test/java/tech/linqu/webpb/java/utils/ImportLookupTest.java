package tech.linqu.webpb.java.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.resolveFile;

import com.google.protobuf.Descriptors.FileDescriptor;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.test.TestUtils;
import tech.linqu.webpb.utilities.utils.Const;

class ImportLookupTest {

    @Test
    void shouldTestUpdateSuccess() {
        RequestContext context = TestUtils.createRequest(Dumps.TEST1);
        ImportLookup lookup = new ImportLookup();
        FileDescriptor fileDescriptor = resolveFile(context.getDescriptors(), Const.WEBPB_OPTIONS);
        lookup.update(fileDescriptor);
        assertFalse(lookup.getNames().isEmpty());

        assertThrows(RuntimeException.class, () -> lookup.update(fileDescriptor),
            "Duplicated import: com.fasterxml.jackson.annotation.JsonIgnoreProperties");
    }
}
