/*
 * Copyright (c) 2020 linqu.tech, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.linqu.webpb.runtime.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Permit {

    private Permit() {
    }

    private static final long ACCESSIBLE_OVERRIDE_FIELD_OFFSET;

    private static final IllegalAccessException INIT_ERROR;

    static {
        long g;
        Throwable ex;

        try {
            g = getOverrideFieldOffset();
            ex = null;
        } catch (Throwable t) {
            g = -1L;
            ex = t;
        }

        ACCESSIBLE_OVERRIDE_FIELD_OFFSET = g;
        if (ex == null) {
            INIT_ERROR = null;
        } else if (ex instanceof IllegalAccessException) {
            INIT_ERROR = (IllegalAccessException) ex;
        } else {
            INIT_ERROR = new IllegalAccessException("Cannot initialize Unsafe-based permit");
            INIT_ERROR.initCause(ex);
        }
    }

    public static <T extends AccessibleObject> T setAccessible(T accessor) {
        if (INIT_ERROR == null) {
            Unsafe.putBoolean(accessor, ACCESSIBLE_OVERRIDE_FIELD_OFFSET, true);
        } else {
            accessor.setAccessible(true);
        }

        return accessor;
    }

    private static long getOverrideFieldOffset() throws Throwable {
        Field f = null;
        Throwable saved = null;
        try {
            f = AccessibleObject.class.getDeclaredField("override");
        } catch (Throwable t) {
            saved = t;
        }

        if (f != null) {
            return Unsafe.objectFieldOffset(f);
        }
        // The below seems very risky, but for all AccessibleObjects in java today it does work, and starting with JDK12, making the field accessible is no longer possible.
        try {
            return Unsafe.objectFieldOffset(Fake.class.getDeclaredField("override"));
        } catch (Throwable t) {
            throw saved;
        }
    }

    static class Fake {

        boolean override;
    }

    public static Method getMethod(Class<?> c, String mName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method m = null;
        Class<?> oc = c;
        while (c != null) {
            try {
                m = c.getDeclaredMethod(mName, parameterTypes);
                break;
            } catch (NoSuchMethodException ignored) {
            }
            c = c.getSuperclass();
        }

        if (m == null) {
            throw new NoSuchMethodException(oc == null ? "" : oc.getName() + " :: " + mName + "(args)");
        }
        return setAccessible(m);
    }
}

