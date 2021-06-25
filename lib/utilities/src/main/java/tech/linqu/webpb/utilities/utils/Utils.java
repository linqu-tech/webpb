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

    /**
     * Make sure tail newline count not greater than limit.
     *
     * @param builder {@link StringBuilder}
     * @param limit   limit
     */
    public static void limitNewline(StringBuilder builder, int limit) {
        int index = builder.length();
        while (--index >= 0) {
            char ch = builder.charAt(index);
            if (ch != '\n') {
                break;
            }
            if (builder.length() - index > limit) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
    }

    /**
     * Make sure there are count of tail newlines.
     *
     * @param builder {@link StringBuilder}
     * @param count   count
     */
    public static void alignNewline(StringBuilder builder, int count) {
        limitNewline(builder, count);
        if (count <= 0) {
            return;
        }
        while (builder.length() < count || builder.charAt(builder.length() - count) != '\n') {
            builder.append('\n');
        }
    }
}
