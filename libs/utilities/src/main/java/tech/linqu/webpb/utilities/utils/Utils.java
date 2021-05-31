package tech.linqu.webpb.utilities.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Utils {

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
        }
        String tmp = StringUtils.stripStart(path, "/");
        if (StringUtils.isEmpty(tmp)) {
            return "";
        }
        return "/" + StringUtils.stripEnd(tmp, "/");
    }
}
