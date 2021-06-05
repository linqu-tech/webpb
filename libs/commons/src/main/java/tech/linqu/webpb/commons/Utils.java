package tech.linqu.webpb.commons;

import java.util.concurrent.Callable;

/**
 * Common utilities.
 */
public class Utils {

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
     * Return orelse if value is empty.
     *
     * @param value  string value
     * @param orelse default value
     * @return string
     */
    public static String emptyOrDefault(String value, String orelse) {
        return (value == null || value.isEmpty()) ? orelse : value;
    }
}
