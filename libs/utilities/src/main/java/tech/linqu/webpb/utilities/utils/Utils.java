package tech.linqu.webpb.utilities.utils;

import org.apache.commons.lang3.StringUtils;

public class Utils {

    public static String normalize(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        String tmp = StringUtils.stripStart(path, "/");
        if (StringUtils.isEmpty(tmp)) {
            return "";
        }
        return "/" + StringUtils.stripEnd(tmp, "/");
    }
}
