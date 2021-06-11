package tech.linqu.webpb.java;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
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

    /**
     * Compare text to a file content.
     *
     * @param text     text to compare
     * @param filename file to compare
     * @return true if same
     */
    public static boolean compareToFile(String text, String filename) {
        try {
            InputStream inputStream = TestUtils.class.getResourceAsStream(filename);
            if (inputStream == null) {
                throw new NullPointerException("file not exists: " + filename);
            }
            String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return text.equals(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
