package tech.linqu.webpb.java.generator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.linqu.webpb.utilities.test.TestUtils.compareToFile;
import static tech.linqu.webpb.utilities.test.TestUtils.createRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;

class GeneratorTest {

    @Test
    void test() {
        List<Dumps> dumpsList = new ArrayList<>(Arrays.asList(Dumps.values()));
        dumpsList.remove(Dumps.TEST3);
        for (Dumps dumps : dumpsList) {
            RequestContext context = createRequest(dumps);
            Map<String, String> fileMap = Generator.create().generate(context);
            String prefix = "/" + dumps.name().toLowerCase() + "/";
            for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                String filename = prefix + entry.getKey();
                assertTrue(compareToFile(entry.getValue(), filename, false));
            }
        }
    }

    @Test
    void test7() {
        RequestContext context = createRequest(Dumps.TEST3);
        assertThrows(RuntimeException.class,
            () -> Generator.create().generate(context), "Bad import: a..b");
    }
}
