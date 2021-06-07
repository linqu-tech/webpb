package tech.linqu.webpb.processor.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class UnsafeTest {

    private static class Fake {

        boolean first;
    }

    @Test
    void shouldCallObjectFieldOffsetSuccess() throws NoSuchFieldException {
        assertEquals(12, Unsafe.objectFieldOffset(Fake.class.getDeclaredField("first")));
    }

    @Disabled
    @Test
    void shouldThrowExceptionWhenCallObjectFieldOffsetError() throws Throwable {
        Field field = Unsafe.class.getDeclaredField("objectFieldOffset");
        field.setAccessible(true);
        MethodHandle methodHandle = spy((MethodHandle) field.get(null));
        doThrow(new RuntimeException("ERROR")).when(methodHandle).invokeExact();
        field.set(null, methodHandle);
        assertThrows(RuntimeException.class, () -> Unsafe.objectFieldOffset(field), "ERROR");
    }

    @Test
    void shouldCallPutBooleanSuccess() throws NoSuchFieldException {
        long offset = Unsafe.objectFieldOffset(Fake.class.getDeclaredField("first"));
        Fake fake = new Fake();
        Unsafe.putBoolean(fake, offset, true);
        assertTrue(fake.first);
    }

    @Test
    void shouldPutBooleanVolatileSuccess() throws NoSuchFieldException {
        long offset = Unsafe.objectFieldOffset(Fake.class.getDeclaredField("first"));
        Fake fake = new Fake();
        Unsafe.putBooleanVolatile(fake, offset, true);
        assertTrue(fake.first);
    }

    @Test
    void shouldSneakyThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            throw Unsafe.sneaky(null);
        });
    }

    @Test
    void shouldSneakyThrowRuntimeException() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw Unsafe.sneaky(new IllegalArgumentException("ERROR"));
        }, "ERROR");
    }
}
