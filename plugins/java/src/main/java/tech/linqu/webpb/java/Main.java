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

package tech.linqu.webpb.java;

import com.github.javaparser.ast.CompilationUnit;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.java.generator.Generator;
import tech.linqu.webpb.java.generator.NameMap;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;

/**
 * The main class.
 */
public class Main {

    /**
     * The main method.
     */
    public static void main(String[] args) throws Exception {
        RequestContext context = new RequestContext(FileOpts::hasJava);
        NameMap nameMap = new NameMap(context.getDescriptors());

        for (FileDescriptor fileDescriptor : context.getTargetDescriptors()) {
            String javaPackage = fileDescriptor.getOptions().getJavaPackage();
            if (shouldIgnore(javaPackage)) {
                continue;
            }
            for (Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                CompilationUnit compilationUnit = Generator
                    .of(context, fileDescriptor, nameMap, Collections.emptyList())
                    .generateMessage(descriptor);
                writeCompilationUnit(compilationUnit, filename(javaPackage, descriptor.getName()));
            }
            for (Descriptors.EnumDescriptor enumDescriptor : fileDescriptor.getEnumTypes()) {
                CompilationUnit compilationUnit = Generator
                    .of(context, fileDescriptor, nameMap, Collections.emptyList())
                    .generateEnum(enumDescriptor);
                writeCompilationUnit(compilationUnit,
                    filename(javaPackage, enumDescriptor.getName()));
            }
        }
    }

    private static boolean shouldIgnore(String packageName) {
        return StringUtils.isEmpty(packageName) || "com.google.protobuf".equals(packageName);
    }

    private static String filename(String javaPackage, String className) {
        return javaPackage.replaceAll("\\.", "/") + "/" + className + ".java";
    }

    private static void writeCompilationUnit(CompilationUnit compilationUnit, String filename)
        throws Exception {
        if (compilationUnit == null) {
            return;
        }
        String formatted = compilationUnit.toString();
        CodeGeneratorResponse.Builder builder = CodeGeneratorResponse.newBuilder();
        builder.addFileBuilder()
            .setName(filename)
            .setContent(formatted);
        CodeGeneratorResponse response = builder.build();
        response.writeTo(System.out);
    }
}
