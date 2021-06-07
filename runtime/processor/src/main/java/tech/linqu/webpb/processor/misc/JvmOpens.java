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

import java.lang.reflect.Method;

/**
 * Utilities to open JVM modules.
 */
public class JvmOpens {

    /**
     * Add opens for specified class.
     *
     * @param type target class type.
     */
    public static void addOpens(Class<?> type) {
        Class<?> classModule;
        try {
            classModule = Class.forName("java.lang.Module");
        } catch (ClassNotFoundException e) {
            return; //jdk8-; this is not needed.
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
            Method m = classModule.getDeclaredMethod("implAddOpens", String.class, classModule);
            long firstFieldOffset = getFirstFieldOffset();
            Unsafe.putBooleanVolatile(m, firstFieldOffset, true);
            for (String p : packages) {
                m.invoke(jdkCompilerModule, p, ownModule);
            }
        } catch (Exception ignored) {
            // ignored
        }
    }

    private static long getFirstFieldOffset() {
        try {
            return Unsafe.objectFieldOffset(Parent.class.getDeclaredField("first"));
        } catch (NoSuchFieldException | SecurityException e) {
            // can't happen.
            throw new RuntimeException(e);
        }
    }

    private static Object getJdkCompilerModule() {
        try {
            Class<?> classModuleLayer = Class.forName("java.lang.ModuleLayer");
            Method methodBoot = classModuleLayer.getDeclaredMethod("boot");
            Object bootLayer = methodBoot.invoke(null);
            Class<?> classOptional = Class.forName("java.util.Optional");
            Method findModule = classModuleLayer.getDeclaredMethod("findModule", String.class);
            Object compiler = findModule.invoke(bootLayer, "jdk.compiler");
            return classOptional.getDeclaredMethod("get").invoke(compiler);
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
