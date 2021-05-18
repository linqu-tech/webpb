package tech.linqu.webpb.runtime.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Unsafe {

    private static final MethodHandle objectFieldOffset;

    private static final MethodHandle putBoolean;

    private static final MethodHandle putBooleanVolatile;

    static {
        try {
            Class<?> Unsafe = Class.forName("sun.misc.Unsafe");
            Object theUnsafe = null;
            {
                int mods = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
                for (Field field : Unsafe.getDeclaredFields()) {
                    if (field.getModifiers() == mods && field.getType() == Unsafe) {
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
            }

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            objectFieldOffset = lookup.findVirtual(Unsafe, "objectFieldOffset", MethodType.methodType(long.class, Field.class)).bindTo(theUnsafe);
            putBoolean = lookup.findVirtual(Unsafe, "putBoolean", MethodType.methodType(void.class, Object.class, long.class, boolean.class)).bindTo(theUnsafe);
            putBooleanVolatile = lookup.findVirtual(Unsafe, "putBooleanVolatile", MethodType.methodType(void.class, Object.class, long.class, boolean.class)).bindTo(theUnsafe);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to setup Unsafe", e);
        }
    }

    public static long objectFieldOffset(Field f) {
        try {
            return (long) objectFieldOffset.invokeExact(f);
        } catch (Throwable t) {
            throw sneaky(t);
        }
    }

    public static void putBoolean(Object o, long offset, boolean x) {
        try {
            putBoolean.invokeExact(o, offset, x);
        } catch (Throwable t) {
            throw sneaky(t);
        }
    }

    public static void putBooleanVolatile(Object o, long offset, boolean x) {
        try {
            putBooleanVolatile.invokeExact(o, offset, x);
        } catch (Throwable t) {
            throw sneaky(t);
        }
    }

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
