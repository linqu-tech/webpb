package tech.linqu.webpb.java.generator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.java.TestUtils;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;

class GeneratorTest {

    @Test
    void test() {
        for (Dumps dumps : Dumps.values()) {
            RequestContext context = TestUtils.createRequest(dumps);
            Map<String, String> fileMap = Generator.create().generate(context);
            String prefix = "/" + dumps.name().toLowerCase() + "/";
            for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                String filename = prefix + entry.getKey().replaceAll("\\.java$", ".txt");
                assertTrue(TestUtils.compareToFile(entry.getValue(), filename));
            }
        }
    }
}
