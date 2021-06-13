package tech.linqu.webpb.ts.generator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.linqu.webpb.utilities.test.TestUtils.compareToFile;
import static tech.linqu.webpb.utilities.test.TestUtils.createRequest;

import com.google.protobuf.Descriptors.FileDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;

class GeneratorTest {

    @Test
    void test() {
        for (Dumps dumps : Dumps.values()) {
            RequestContext context = createRequest(dumps);
            for (FileDescriptor fileDescriptor : context.getTargetDescriptors()) {
                String content = Generator.create().generate(context, fileDescriptor);
                if (StringUtils.isEmpty(content)) {
                    continue;
                }
                String prefix = "/" + dumps.name().toLowerCase() + "/";
                String filename = prefix + fileDescriptor.getPackage() + ".ts";
                assertTrue(compareToFile(content, filename, false));
            }
        }
    }
}
