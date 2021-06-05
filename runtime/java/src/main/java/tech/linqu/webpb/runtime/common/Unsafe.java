package tech.linqu.webpb.runtime.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Proxy for sun.misc.Unsafe class.
 */
public class Unsafe {

    private static final MethodHandle objectFieldOffset;

    private static final MethodHandle putBoolean;

    private static final MethodHandle putBooleanVolatile;

    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Object theUnsafe = null;

            int mods = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
            for (Field field : unsafeClass.getDeclaredFields()) {
                if (field.getModifiers() == mods && field.getType() == unsafeClass) {
                    field.setAccessible(true);
                    Object unsafe = field.get(null);
                    if (unsafe != null) {
                        theUnsafe = unsafe;
                        break;
                    }
                }
            }
            if (theUnsafe == null) {
                throw new RuntimeException("Failed to locate Unsafe instance");
            }

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            objectFieldOffset = lookup.findVirtual(unsafeClass, "objectFieldOffset",
                MethodType.methodType(long.class, Field.class)).bindTo(theUnsafe);
            putBoolean = lookup.findVirtual(unsafeClass, "putBoolean",
                MethodType.methodType(void.class, Object.class, long.class, boolean.class))
                .bindTo(theUnsafe);
            putBooleanVolatile = lookup.findVirtual(unsafeClass, "putBooleanVolatile",
                MethodType.methodType(void.class, Object.class, long.class, boolean.class))
                .bindTo(theUnsafe);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to setup Unsafe", e);
        }
    }

    /**
     * See also {@link sun.misc.Unsafe#objectFieldOffset}.
     *
     * @param f filed
     * @return offset of the field
     */
    public static long objectFieldOffset(Field f) {
        try {
            return (long) objectFieldOffset.invokeExact(f);
        } catch (Throwable t) {
            throw sneaky(t);
        }
    }

    /**
     * See also {@link sun.misc.Unsafe#putBoolean}.
     *
     * @param o      Object
     * @param offset offset to put
     * @param x      value
     */
    public static void putBoolean(Object o, long offset, boolean x) {
        try {
            putBoolean.invokeExact(o, offset, x);
        } catch (Throwable t) {
            throw sneaky(t);
        }
    }

    /**
     * See also {@link sun.misc.Unsafe#putBooleanVolatile}.
     *
     * @param o      Object
     * @param offset offset to put
     * @param x      value
     */
    public static void putBooleanVolatile(Object o, long offset, boolean x) {
        try {
            putBooleanVolatile.invokeExact(o, offset, x);
        } catch (Throwable t) {
            throw sneaky(t);
        }
    }

    /**
     * Sneaky exceptions.
     *
     * @param t {@link Throwable}
     * @return {@link RuntimeException}
     */
    public static RuntimeException sneaky(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t");
        }
        return sneaky0(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneaky0(Throwable t) throws T {
        throw (T) t;
    }
}
