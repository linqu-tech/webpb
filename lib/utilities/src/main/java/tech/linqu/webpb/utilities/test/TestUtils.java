package tech.linqu.webpb.utilities.test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import tech.linqu.webpb.tests.Dumps;
import tech.linqu.webpb.utilities.context.RequestContext;

/**
 * Utilities for test.
 */
public class TestUtils {

    private TestUtils() {
    }

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
     * @param exactly  ignore spaces/tabs/newlines when not exactly
     * @return true if same
     */
    public static boolean compareToFile(String text, String filename, boolean exactly) {
        try {
            InputStream inputStream = TestUtils.class.getResourceAsStream(filename);
            if (inputStream == null) {
                throw new NullPointerException("file not exists: " + filename);
            }
            String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            if (exactly) {
                return text.equals(content);
            }
            return text.replaceAll("\\s+", "").equals(content.replaceAll("\\s+", ""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
