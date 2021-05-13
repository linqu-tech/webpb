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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.utils.Const;
import tech.linqu.webpb.utilities.utils.DescriptorUtils;
import tech.linqu.webpb.utilities.utils.OptionUtils;
import tech.linqu.webpb.utilities.utils.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.JavaFieldOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.JavaFileOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.JavaMessageOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.MessageOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.OptFieldOpts;
import tech.linqu.webpb.utilities.utils.WebpbExtend.OptMessageOpts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class MessageGenerator {

    private static final Map<FieldDescriptor.Type, Type> TYPES = new HashMap<FieldDescriptor.Type, Type>() {{
        put(FieldDescriptor.Type.BOOL, new ClassOrInterfaceType(null, Boolean.class.getSimpleName()));
        put(FieldDescriptor.Type.BYTES, new ArrayType(PrimitiveType.byteType()));
        put(FieldDescriptor.Type.DOUBLE, new ClassOrInterfaceType(null, Double.class.getSimpleName()));
        put(FieldDescriptor.Type.FLOAT, new ClassOrInterfaceType(null, Float.class.getSimpleName()));
        put(FieldDescriptor.Type.FIXED32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        put(FieldDescriptor.Type.FIXED64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        put(FieldDescriptor.Type.INT32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        put(FieldDescriptor.Type.INT64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        put(FieldDescriptor.Type.SFIXED32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        put(FieldDescriptor.Type.SFIXED64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        put(FieldDescriptor.Type.SINT32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        put(FieldDescriptor.Type.SINT64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        put(FieldDescriptor.Type.STRING, new ClassOrInterfaceType(null, String.class.getSimpleName()));
        put(FieldDescriptor.Type.UINT32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        put(FieldDescriptor.Type.UINT64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
    }};

    private static final JavaParser JAVA_PARSER = new JavaParser();

    private final RequestContext requestContext;

    private final FileDescriptor fileDescriptor;

    private final List<Name> imports;

    private final NameMap nameMap;

    public ClassOrInterfaceDeclaration generate(Descriptor descriptor) {
        ClassOrInterfaceDeclaration declaration = new ClassOrInterfaceDeclaration();
        declaration.setName(descriptor.getName());
        declaration.addModifier(Modifier.Keyword.PUBLIC);

        addWebpbMeta(descriptor, declaration);

        JavaFileOpts webpbOpts = requestContext.getFileOpts().getJava();
        addAnnotations(declaration, webpbOpts.getAnnotationList());
        JavaFileOpts fileOpts = OptionUtils.getOpts(fileDescriptor, FileOpts::hasJava).getJava();
        addAnnotations(declaration, fileOpts.getAnnotationList());
        JavaMessageOpts messageOpts = OptionUtils.getOpts(descriptor, MessageOpts::hasJava).getJava();
        addAnnotations(declaration, messageOpts.getAnnotationList());

        generateMessageFields(declaration, descriptor);
        generateNested(declaration, descriptor);
        return declaration;
    }

    private void generateNested(ClassOrInterfaceDeclaration declaration, Descriptor descriptor) {
        Set<String> mapFields = descriptor.getFields().stream()
            .filter(FieldDescriptor::isMapField)
            .map(fieldDescriptor -> StringUtils.capitalize(fieldDescriptor.getName()) + "Entry")
            .collect(Collectors.toSet());
        for (Descriptor nestedDescriptor : descriptor.getNestedTypes()) {
            if (mapFields.contains(nestedDescriptor.getName())) {
                return;
            }
            TypeDeclaration<?> typeDeclaration = generate(nestedDescriptor);
            typeDeclaration.addModifier(Modifier.Keyword.STATIC);
            declaration.addMember(typeDeclaration);
        }
    }

    private void addWebpbMeta(Descriptor descriptor, ClassOrInterfaceDeclaration declaration) {
        declaration.addImplementedType("WebpbMessage");
        JAVA_PARSER.parseName(Const.RUNTIME_PACKAGE + ".WebpbMessage")
            .ifSuccessful(imports::add);

        ClassOrInterfaceType metaType = new ClassOrInterfaceType(null, "WebpbMeta");
        JAVA_PARSER.parseName(Const.RUNTIME_PACKAGE + ".WebpbMeta").ifSuccessful(imports::add);

        OptMessageOpts messageOpts = OptionUtils.getOpts(descriptor, MessageOpts::hasOpt).getOpt();
        addStaticOption(declaration, "WEBPB_METHOD", messageOpts.getMethod());
        addStaticOption(declaration, "WEBPB_CONTEXT", messageOpts.getContext());
        addStaticOption(declaration, "WEBPB_PATH", messageOpts.getPath());

        ObjectCreationExpr creationExpr = new ObjectCreationExpr(
            null, new ClassOrInterfaceType(metaType, "Builder"), new NodeList<>()
        );

        MethodCallExpr callExpr = new MethodCallExpr(
            creationExpr, "method", new NodeList<>(new NameExpr("WEBPB_METHOD"))
        );
        callExpr = new MethodCallExpr(
            callExpr, "context", new NodeList<>(new NameExpr("WEBPB_CONTEXT"))
        );
        callExpr = new MethodCallExpr(
            callExpr, "path", new NodeList<>(new NameExpr("WEBPB_PATH"))
        );
        if (!messageOpts.getTagList().isEmpty()) {
            callExpr = new MethodCallExpr(callExpr, "tags",
                new NodeList<>(messageOpts.getTagList().stream()
                    .map(StringLiteralExpr::new)
                    .collect(Collectors.toList()))
            );
        }
        callExpr = new MethodCallExpr(callExpr, "build");

        FieldDeclaration field = declaration.addFieldWithInitializer(metaType, "WEBPB_META", callExpr);
        field.setModifiers(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);
        addWebpbMetaMethod(declaration);
    }

    private void addStaticOption(ClassOrInterfaceDeclaration declaration, String key, String value) {
        value = StringUtils.isEmpty(value) ? "" : value;
        declaration.addFieldWithInitializer(String.class, key, new StringLiteralExpr(value),
            Modifier.Keyword.PUBLIC,
            Modifier.Keyword.STATIC,
            Modifier.Keyword.FINAL
        );
    }

    private void addWebpbMetaMethod(ClassOrInterfaceDeclaration declaration) {
        declaration.addMethod("webpbMeta")
            .addAnnotation(new MarkerAnnotationExpr("Override"))
            .setModifiers(Modifier.Keyword.PUBLIC)
            .setType("WebpbMeta")
            .setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt("WEBPB_META"))));
    }

    private void addAnnotations(BodyDeclaration<?> declaration, List<String> annotations) {
        if (annotations != null && !annotations.isEmpty()) {
            for (String annotation : annotations) {
                JAVA_PARSER.parseAnnotation(annotation).ifSuccessful(expr -> {
                    parseImports(expr, imports);
                    expr.getName().setQualifier(null);
                    declaration.addAnnotation(expr);
                });
            }
        }
    }

    private void parseImports(Node node, List<Name> imports) {
        if (node instanceof FieldAccessExpr) {
            imports.add(nameMap.getFullName(new Name(null, ((FieldAccessExpr) node).getScope().toString())));
        } else if (node instanceof AnnotationExpr) {
            imports.add(nameMap.getFullName(((AnnotationExpr) node).getName()));
        } else if (node instanceof ClassOrInterfaceType) {
            addImports((ClassOrInterfaceType) node, imports);
        } else {
            nameMap.getName(node.toString()).ifPresent(imports::add);
        }
        for (Node childNode : node.getChildNodes()) {
            parseImports(childNode, imports);
        }
    }

    private void generateMessageFields(ClassOrInterfaceDeclaration declaration, Descriptor descriptor) {
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            OptFieldOpts fieldOpts = OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasOpt).getOpt();
            if (fieldOpts.getOmitted()) {
                continue;
            }

            Type fieldType = getType(fieldDescriptor);
            parseImports(fieldType, imports);
            FieldDeclaration fieldDeclaration = declaration.addField(
                fieldType, fieldDescriptor.getName(), Modifier.Keyword.PRIVATE
            );
            JavaFieldOpts javaFieldOpts = OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasJava).getJava();
            List<String> annotations = new ArrayList<>(javaFieldOpts.getAnnotationList());
            if (fieldOpts.getInQuery()) {
                annotations.add(Const.RUNTIME_PACKAGE + ".mvc.InQuery");
            }
            addAnnotations(fieldDeclaration, annotations);
        }
    }

    private Type getType(FieldDescriptor fieldDescriptor) {
        if (fieldDescriptor.isRepeated() && !fieldDescriptor.isMapField()) {
            return new ClassOrInterfaceType(null,
                new SimpleName(List.class.getSimpleName()),
                NodeList.nodeList(toType(fieldDescriptor))
            );
        } else {
            return toType(fieldDescriptor);
        }
    }

    private Type toType(FieldDescriptor fieldDescriptor) {
        if (fieldDescriptor.isMapField()) {
            FieldDescriptor keyDescriptor = DescriptorUtils.getKeyDescriptor(fieldDescriptor);
            FieldDescriptor valueDescriptor = DescriptorUtils.getValueDescriptor(fieldDescriptor);
            return new ClassOrInterfaceType(null, new SimpleName(Map.class.getSimpleName()),
                NodeList.nodeList(toType(keyDescriptor), toType(valueDescriptor))
            );
        }
        Type type = TYPES.get(fieldDescriptor.getType());
        if (type != null) {
            return type.clone();
        }
        return new ClassOrInterfaceType(null, DescriptorUtils.getFieldTypeSimpleName(fieldDescriptor));
    }

    private void addImports(ClassOrInterfaceType type, List<Name> imports) {
        nameMap.getName(type.getNameAsString()).ifPresent(imports::add);
        type.getTypeArguments().ifPresent(list -> {
            for (Type t : list) {
                if (t instanceof ClassOrInterfaceType) {
                    addImports((ClassOrInterfaceType) t, imports);
                }
            }
        });
    }
}
