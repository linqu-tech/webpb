package tech.linqu.webpb.utilities.utils;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities.
 */
public class Utils {

    private Utils() {
    }

    /**
     * Normalize a path.
     *
     * @param path target path
     * @return normalized path
     */
    public static String normalize(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        try {
            new URL(path);
            return StringUtils.stripEnd(path, "/");
        } catch (MalformedURLException ignored) {
            // ignored
        }
        String tmp = StringUtils.stripStart(path, "/");
        if (StringUtils.isEmpty(tmp)) {
            return "";
        }
        return "/" + StringUtils.stripEnd(tmp, "/");
    }
}
