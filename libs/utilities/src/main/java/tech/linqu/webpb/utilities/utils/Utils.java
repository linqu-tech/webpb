package tech.linqu.webpb.utilities.utils;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities.
 */
public class Utils {

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
            URL url = new URL(path);
            if (isNotEmpty(url.getProtocol()) || isNotEmpty(url.getHost())) {
                return StringUtils.stripEnd(path, "/");
            }
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
