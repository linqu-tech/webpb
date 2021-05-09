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
package tech.linqu.webpb.java.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Name;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.utils.WebpbExtend.EnumOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.MessageOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.OptEnumOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.OptMessageOpts;
import tech.linqu.webpb.utilities.utils.Const;
import tech.linqu.webpb.utilities.utils.OptionUtils;
import com.google.protobuf.Descriptors.Descriptor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static tech.linqu.webpb.utilities.utils.OptionUtils.getOpts;
import static com.google.protobuf.Descriptors.EnumDescriptor;
import static com.google.protobuf.Descriptors.FileDescriptor;

@RequiredArgsConstructor(staticName = "of")
public final class Generator {

    private final RequestContext requestContext;

    private final FileDescriptor fileDescriptor;

    private final NameMap nameMap;

    private final List<String> tags;

    public CompilationUnit generateMessage(Descriptor descriptor) {
        OptMessageOpts messageOpts = getOpts(descriptor, MessageOpts::hasOpt).getOpt();
        if (OptionUtils.shouldSkip(messageOpts.getTagList(), this.tags)) {
            return null;
        }
        CompilationUnit compilationUnit = createCompilationUnit(fileDescriptor);
        List<Name> imports = new ArrayList<>();
        TypeDeclaration<?> declaration = MessageGenerator
            .of(requestContext, fileDescriptor, imports, nameMap)
            .generate(descriptor);
        imports.sort(Comparator.comparing(Name::asString));
        imports.forEach(i -> compilationUnit.addImport(i.asString()));
        compilationUnit.addType(declaration);
        return compilationUnit;
    }

    public CompilationUnit generateEnum(EnumDescriptor enumDescriptor) {
        OptEnumOpts optEnumOpts = getOpts(enumDescriptor, EnumOpts::hasOpt).getOpt();
        if (OptionUtils.shouldSkip(optEnumOpts.getTagList(), this.tags)) {
            return null;
        }
        CompilationUnit compilationUnit = createCompilationUnit(fileDescriptor);
        TypeDeclaration<?> declaration = EnumGenerator.create().generate(enumDescriptor);
        compilationUnit.addType(declaration);
        return compilationUnit;
    }

    private CompilationUnit createCompilationUnit(FileDescriptor fileDescriptor) {
        CompilationUnit unit = new CompilationUnit();
        unit.addOrphanComment(new LineComment(Const.HEADER));
        unit.addOrphanComment(new LineComment(Const.GIT_URL));
        String javaPackage = fileDescriptor.getOptions().getJavaPackage();
        if (StringUtils.isEmpty(javaPackage)) {
            throw new IllegalArgumentException("java_package option is required in " + fileDescriptor.getFullName());
        }
        unit.setPackageDeclaration(javaPackage);
        return unit;
    }
}
