package tech.linqu.webpb.utilities.context;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import tech.linqu.webpb.tests.Dumps;

class RequestContextTest {

    @Test
    void success() throws Exception {
        for (Dumps dumps : Dumps.values()) {
            dumps.pipe();
            RequestContext context = new RequestContext(opts -> true);
            assertFalse(context.getDescriptors().isEmpty());
            assertFalse(context.getTargetDescriptors().isEmpty());
            assertNotNull(context.getFileOpts());
        }
    }
}
