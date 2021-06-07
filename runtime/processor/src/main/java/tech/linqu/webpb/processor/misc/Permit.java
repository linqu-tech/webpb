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

package tech.linqu.webpb.processor.misc;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility to grant reflection permit.
 */
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

    private static <T extends AccessibleObject> T setAccessible(T accessor) {
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
        try {
            return Unsafe.objectFieldOffset(Fake.class.getDeclaredField("override"));
        } catch (Throwable t) {
            throw saved;
        }
    }

    static class Fake {

        boolean override;
    }

    /**
     * Return a method by reflection.
     *
     * @param c              target class type
     * @param moduleName     module name
     * @param parameterTypes type array of the method
     * @return the Method object
     * @throws NoSuchMethodException if method not found
     */
    public static Method getMethod(Class<?> c, String moduleName, Class<?>... parameterTypes)
        throws NoSuchMethodException {
        Method m = null;
        Class<?> oc = c;
        while (c != null) {
            try {
                m = c.getDeclaredMethod(moduleName, parameterTypes);
                break;
            } catch (NoSuchMethodException ignored) {
                // ignored
            }
            c = c.getSuperclass();
        }

        if (m == null) {
            throw new NoSuchMethodException(
                oc == null ? "" : oc.getName() + " :: " + moduleName + "(args)");
        }
        return setAccessible(m);
    }
}

