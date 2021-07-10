package tech.linqu.webpb.commons;

import java.util.concurrent.Callable;

/**
 * Common utilities.
 */
public class Utils {

    private Utils() {
    }

    /**
     * Sneaky exceptions.
     *
     * @param callable {@link Callable}
     * @param <T>      T
     * @return T
     */
    public static <T> T uncheckedCall(Callable<T> callable) {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return an empty string if value is null.
     *
     * @param value string value
     * @return string
     */
    public static String orEmpty(String value) {
        return (value == null) ? "" : value;
    }
}
