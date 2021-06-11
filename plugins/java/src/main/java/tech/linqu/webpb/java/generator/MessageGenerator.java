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

import static com.google.protobuf.Descriptors.FieldDescriptor.Type.BOOL;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.BYTES;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.DOUBLE;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.FIXED32;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.FIXED64;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.FLOAT;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.INT32;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.INT64;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.SFIXED32;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.SFIXED64;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.SINT32;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.SINT64;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.STRING;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.UINT32;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.UINT64;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getFieldTypeSimpleName;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getMapKeyDescriptor;
import static tech.linqu.webpb.utilities.utils.DescriptorUtils.getMapValueDescriptor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.utilities.context.RequestContext;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.JavaFieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.JavaFileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.JavaMessageOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.MessageOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptFieldOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptMessageOpts;
import tech.linqu.webpb.utilities.utils.Const;
import tech.linqu.webpb.utilities.utils.OptionUtils;
import tech.linqu.webpb.utilities.utils.Utils;

/**
 * Generator for {@link Descriptor}.
 */
@RequiredArgsConstructor(staticName = "of")
public class MessageGenerator {

    private static final Map<FieldDescriptor.Type, Type> TYPES;

    static {
        Map<FieldDescriptor.Type, Type> map = new HashMap<>();
        map.put(BOOL, new ClassOrInterfaceType(null, Boolean.class.getSimpleName()));
        map.put(BYTES, new ArrayType(PrimitiveType.byteType()));
        map.put(DOUBLE, new ClassOrInterfaceType(null, Double.class.getSimpleName()));
        map.put(FLOAT, new ClassOrInterfaceType(null, Float.class.getSimpleName()));
        map.put(FIXED32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        map.put(FIXED64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        map.put(INT32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        map.put(INT64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        map.put(SFIXED32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        map.put(SFIXED64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        map.put(SINT32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        map.put(SINT64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        map.put(STRING, new ClassOrInterfaceType(null, String.class.getSimpleName()));
        map.put(UINT32, new ClassOrInterfaceType(null, Integer.class.getSimpleName()));
        map.put(UINT64, new ClassOrInterfaceType(null, Long.class.getSimpleName()));
        TYPES = map;
    }

    private static final JavaParser JAVA_PARSER = new JavaParser();

    private final RequestContext requestContext;

    private final FileDescriptor fileDescriptor;

    private final List<Name> imports;

    private final NameMap nameMap;

    /**
     * Entrance of the generator.
     *
     * @param descriptor {@link Descriptor}.
     * @return {@link ClassOrInterfaceDeclaration}
     */
    public ClassOrInterfaceDeclaration generate(Descriptor descriptor) {
        ClassOrInterfaceDeclaration declaration = new ClassOrInterfaceDeclaration();
        declaration.setName(descriptor.getName());
        declaration.addModifier(Modifier.Keyword.PUBLIC);

        addWebpbMeta(descriptor, declaration);

        JavaFileOpts webpbOpts = requestContext.getFileOpts().getJava();
        addAnnotations(declaration, webpbOpts.getAnnotationList());
        JavaFileOpts fileOpts = OptionUtils.getOpts(fileDescriptor, FileOpts::hasJava).getJava();
        addAnnotations(declaration, fileOpts.getAnnotationList());
        JavaMessageOpts messageOpts =
            OptionUtils.getOpts(descriptor, MessageOpts::hasJava).getJava();
        addAnnotations(declaration, messageOpts.getAnnotationList());

        generateMessageFields(declaration, descriptor);
        generateConstructor(declaration, descriptor);
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
                continue;
            }
            TypeDeclaration<?> typeDeclaration = generate(nestedDescriptor);
            typeDeclaration.addModifier(Modifier.Keyword.STATIC);
            declaration.addMember(typeDeclaration);
        }
    }

    private void addWebpbMeta(Descriptor descriptor, ClassOrInterfaceDeclaration declaration) {
        declaration.addImplementedType("WebpbMessage");
        JAVA_PARSER.parseName(Const.RUNTIME_PACKAGE + ".WebpbMessage")
            .ifSuccessful(v -> addImports(imports, v));

        JAVA_PARSER.parseName(Const.RUNTIME_PACKAGE + ".WebpbMeta")
            .ifSuccessful(v -> addImports(imports, v));

        OptMessageOpts messageOpts = OptionUtils.getOpts(descriptor, MessageOpts::hasOpt).getOpt();
        addStaticOption(declaration, "WEBPB_METHOD", messageOpts.getMethod());
        addStaticOption(declaration, "WEBPB_CONTEXT", Utils.normalize(messageOpts.getContext()));
        addStaticOption(declaration, "WEBPB_PATH", Utils.normalize(messageOpts.getPath()));

        ClassOrInterfaceType metaType = new ClassOrInterfaceType(null, "WebpbMeta");
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
        callExpr = new MethodCallExpr(callExpr, "build");

        FieldDeclaration field =
            declaration.addFieldWithInitializer(metaType, "WEBPB_META", callExpr);
        field
            .setModifiers(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);
        addWebpbMetaMethod(declaration);
    }

    private void addStaticOption(ClassOrInterfaceDeclaration declaration, String key,
                                 String value) {
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
        for (String annotation : annotations) {
            JAVA_PARSER.parseAnnotation(annotation).ifSuccessful(expr -> {
                parseImports(expr, imports);
                expr.getName().setQualifier(null);
                declaration.addAnnotation(expr);
            });
        }
    }

    private void parseImports(Node node, List<Name> imports) {
        if (node instanceof FieldAccessExpr) {
            addImports(imports, nameMap
                .getFullName(new Name(null, ((FieldAccessExpr) node).getScope().toString())));
        } else if (node instanceof AnnotationExpr) {
            addImports(imports, nameMap.getFullName(((AnnotationExpr) node).getName()));
        } else if (node instanceof ClassOrInterfaceType) {
            addImports((ClassOrInterfaceType) node, imports);
        } else {
            nameMap.getName(node.toString()).ifPresent(v -> addImports(imports, v));
        }
        for (Node childNode : node.getChildNodes()) {
            parseImports(childNode, imports);
        }
    }

    private void generateConstructor(ClassOrInterfaceDeclaration declaration,
                                     Descriptor descriptor) {
        declaration.addConstructor()
            .setModifiers(Modifier.Keyword.PUBLIC)
            .setBody(new BlockStmt());
        List<FieldDescriptor> descriptors = descriptor.getFields().stream()
            .filter(fieldDescriptor -> {
                OptFieldOpts fieldOpts =
                    OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasOpt).getOpt();
                return !fieldOpts.getOmitted();
            })
            .collect(Collectors.toList());
        if (descriptors.isEmpty() || descriptors.size() > 5) {
            return;
        }
        BlockStmt blockStmt = new BlockStmt();
        ConstructorDeclaration constructor = declaration.addConstructor()
            .setModifiers(Modifier.Keyword.PUBLIC)
            .setBody(blockStmt);
        for (FieldDescriptor fieldDescriptor : descriptors) {
            constructor.addParameter(getType(fieldDescriptor), fieldDescriptor.getName());
            blockStmt.addStatement(new AssignExpr(
                new FieldAccessExpr(new ThisExpr(), fieldDescriptor.getName()),
                new NameExpr(fieldDescriptor.getName()),
                AssignExpr.Operator.ASSIGN
            ));
        }
    }

    private void generateMessageFields(ClassOrInterfaceDeclaration declaration,
                                       Descriptor descriptor) {
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            OptFieldOpts fieldOpts =
                OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasOpt).getOpt();
            if (fieldOpts.getOmitted()) {
                continue;
            }

            Type fieldType = getType(fieldDescriptor);
            parseImports(fieldType, imports);
            FieldDeclaration fieldDeclaration = declaration.addField(
                fieldType, fieldDescriptor.getName(), Modifier.Keyword.PRIVATE
            );
            JavaFieldOpts javaFieldOpts =
                OptionUtils.getOpts(fieldDescriptor, FieldOpts::hasJava).getJava();
            List<String> annotations = new ArrayList<>(javaFieldOpts.getAnnotationList());
            if (fieldOpts.getInQuery()) {
                annotations.add("@" + Const.RUNTIME_PACKAGE + ".common.InQuery");
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
            FieldDescriptor keyDescriptor = getMapKeyDescriptor(fieldDescriptor);
            FieldDescriptor valueDescriptor = getMapValueDescriptor(fieldDescriptor);
            return new ClassOrInterfaceType(null, new SimpleName(Map.class.getSimpleName()),
                NodeList.nodeList(toType(keyDescriptor), toType(valueDescriptor))
            );
        }
        Type type = TYPES.get(fieldDescriptor.getType());
        if (type != null) {
            return type.clone();
        }
        return new ClassOrInterfaceType(null, getFieldTypeSimpleName(fieldDescriptor));
    }

    private void addImports(ClassOrInterfaceType type, List<Name> imports) {
        nameMap.getName(type.getNameAsString()).ifPresent(v -> addImports(imports, v));
        type.getTypeArguments().ifPresent(list -> {
            for (Type t : list) {
                if (t instanceof ClassOrInterfaceType) {
                    addImports((ClassOrInterfaceType) t, imports);
                }
            }
        });
    }

    private void addImports(List<Name> imports, Name name) {
        if (StringUtils.equalsIgnoreCase(name.toString(), "com.google.protobuf.Any")) {
            JAVA_PARSER.parseName(Const.RUNTIME_PACKAGE + ".Any")
                .ifSuccessful(imports::add);
        } else {
            imports.add(name);
        }
    }
}
