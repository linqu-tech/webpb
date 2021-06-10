package tech.linqu.webpb.utilities;

import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;

/**
 * Utilities for test.
 */
public class TestUtils {

    /**
     * Create a {@link RequestContext} from {@link Dumps}.
     *
     * @param dumps {@link Dumps}
     * @return {@link RequestContext}
     */
    public static RequestContext createRequest(Dumps dumps) {
        dumps.pipe();
        try {
            return new RequestContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
