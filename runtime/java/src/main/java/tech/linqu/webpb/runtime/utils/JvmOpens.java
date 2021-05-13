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

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JvmOpens {

    public static void addOpens(Class<?> type) {
        Class<?> cModule;
        try {
            cModule = Class.forName("java.lang.Module");
        } catch (ClassNotFoundException e) {
            return; //jdk8-; this is not needed.
        }

        Unsafe unsafe = getUnsafe();
        if (unsafe == null) {
            return;
        }
        Object jdkCompilerModule = getJdkCompilerModule();
        Object ownModule = getOwnModule(type);
        String[] packages = {
            "com.sun.tools.javac.code",
            "com.sun.tools.javac.comp",
            "com.sun.tools.javac.file",
            "com.sun.tools.javac.main",
            "com.sun.tools.javac.model",
            "com.sun.tools.javac.parser",
            "com.sun.tools.javac.processing",
            "com.sun.tools.javac.tree",
            "com.sun.tools.javac.util",
            "com.sun.tools.javac.jvm",
        };

        try {
            Method m = cModule.getDeclaredMethod("implAddOpens", String.class, cModule);
            long firstFieldOffset = getFirstFieldOffset(unsafe);
            unsafe.putBooleanVolatile(m, firstFieldOffset, true);
            for (String p : packages) {
                m.invoke(jdkCompilerModule, p, ownModule);
            }
        } catch (Exception ignore) {
        }
    }

    private static long getFirstFieldOffset(Unsafe unsafe) {
        try {
            return unsafe.objectFieldOffset(Parent.class.getDeclaredField("first"));
        } catch (NoSuchFieldException | SecurityException e) {
            // can't happen.
            throw new RuntimeException(e);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getJdkCompilerModule() {
		/* call public api: ModuleLayer.boot().findModule("jdk.compiler").get();
		   but use reflection because we don't want this code to crash on jdk1.7 and below.
		   In that case, none of this stuff was needed in the first place, so we just exit via
		   the catch block and do nothing.
		 */

        try {
            Class<?> cModuleLayer = Class.forName("java.lang.ModuleLayer");
            Method mBoot = cModuleLayer.getDeclaredMethod("boot");
            Object bootLayer = mBoot.invoke(null);
            Class<?> cOptional = Class.forName("java.util.Optional");
            Method mFindModule = cModuleLayer.getDeclaredMethod("findModule", String.class);
            Object oCompilerO = mFindModule.invoke(bootLayer, "jdk.compiler");
            return cOptional.getDeclaredMethod("get").invoke(oCompilerO);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getOwnModule(Class<?> type) {
        try {
            Method m = Permit.getMethod(Class.class, "getModule");
            return m.invoke(type);
        } catch (Exception e) {
            return null;
        }
    }
}
