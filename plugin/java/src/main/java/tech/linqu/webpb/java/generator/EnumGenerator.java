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
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import org.apache.commons.lang3.StringUtils;
import tech.linqu.webpb.java.utils.ImportLookup;
import tech.linqu.webpb.java.utils.ImportedName;
import tech.linqu.webpb.java.utils.Imports;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.EnumValueOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.JavaEnumOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.OptEnumValueOpts;
import tech.linqu.webpb.utilities.utils.OptionUtils;

/**
 * Generator for enum definition.
 */
public class EnumGenerator {

    private static final String ENUM_VALUE = "value";

    private Imports imports;

    private boolean stringValue;

    private Type valueType = PrimitiveType.intType();

    /**
     * Generate enum declaration.
     *
     * @param unit           {@link CompilationUnit}
     * @param importLookup   {@link ImportLookup}
     * @param fileDescriptor {@link FileDescriptor}
     * @param descriptor     {@link EnumDescriptor}
     * @return {@link CompilationUnit}
     */
    public CompilationUnit generate(CompilationUnit unit,
                                    ImportLookup importLookup,
                                    FileDescriptor fileDescriptor,
                                    EnumDescriptor descriptor) {
        this.imports = new Imports(new ImportLookup().copy(importLookup).update(fileDescriptor));
        this.stringValue = OptionUtils.isStringValue(descriptor);
        if (this.stringValue) {
            this.valueType = new ClassOrInterfaceType(null, String.class.getSimpleName());
        }
        EnumDeclaration declaration = generate(descriptor);
        imports.computeUnit(unit);
        unit.addType(declaration);
        return unit;
    }

    private EnumDeclaration generate(EnumDescriptor descriptor) {
        EnumDeclaration declaration = new EnumDeclaration();
        declaration.setName(descriptor.getName());
        declaration.addModifier(Modifier.Keyword.PUBLIC);

        JavaEnumOpts enumOpts = OptionUtils.getOpts(descriptor, EnumOpts::hasJava).getJava();
        for (String impl : enumOpts.getImplementsList()) {
            ImportedName importedName = imports.checkAndImport(impl);
            declaration.addImplementedType(importedName.getName().asString());
        }

        for (EnumValueDescriptor valueDescriptor : descriptor.getValues()) {
            EnumConstantDeclaration enumConstant =
                declaration.addEnumConstant(valueDescriptor.getName());
            enumConstant.addArgument(getValueLiteral(valueDescriptor));
        }

        declaration.addField(this.valueType, ENUM_VALUE, Modifier.Keyword.PRIVATE);
        generateEnumConstructor(declaration);
        generateEnumOfMethod(declaration, descriptor);
        generateEnumValueGetter(declaration);
        return declaration;
    }

    private void generateEnumConstructor(EnumDeclaration declaration) {
        ConstructorDeclaration constructor = declaration.addConstructor();
        constructor.addParameter(new Parameter(this.valueType, ENUM_VALUE));
        constructor.getBody().addStatement(new AssignExpr(
            new FieldAccessExpr(new ThisExpr(), ENUM_VALUE),
            new NameExpr(ENUM_VALUE),
            AssignExpr.Operator.ASSIGN
        ));
    }

    private void generateEnumOfMethod(EnumDeclaration declaration, EnumDescriptor descriptor) {
        MethodDeclaration method =
            declaration.addMethod("fromValue", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        method.addParameter(new Parameter(this.valueType, ENUM_VALUE));
        method.setType(declaration.getName().asString());
        NodeList<SwitchEntry> entries = new NodeList<>();
        for (EnumValueDescriptor valueDescriptor : descriptor.getValues()) {
            entries.add(new SwitchEntry(
                NodeList.nodeList(getValueLiteral(valueDescriptor)),
                SwitchEntry.Type.STATEMENT_GROUP,
                NodeList.nodeList(new ReturnStmt(new NameExpr(valueDescriptor.getName())))
            ));
        }
        entries.add(new SwitchEntry().addStatement(new ReturnStmt("null")));
        method.setBody(
            new BlockStmt().addStatement(new SwitchStmt(new NameExpr(ENUM_VALUE), entries)));
    }

    private void generateEnumValueGetter(EnumDeclaration declaration) {
        MethodDeclaration method = declaration
            .addMethod("get" + StringUtils.capitalize(ENUM_VALUE), Modifier.Keyword.PUBLIC);
        method.setType(this.valueType);
        method.setBody(new BlockStmt().addStatement(new ReturnStmt(
            new FieldAccessExpr(new ThisExpr(), ENUM_VALUE)
        )));
    }

    private LiteralExpr getValueLiteral(EnumValueDescriptor valueDescriptor) {
        OptEnumValueOpts opts =
            OptionUtils.getOpts(valueDescriptor, EnumValueOpts::hasOpt).getOpt();
        LiteralExpr literalExpr;
        if (StringUtils.isEmpty(opts.getValue())) {
            if (this.stringValue) {
                literalExpr = new IntegerLiteralExpr("\"" + valueDescriptor.getName() + "\"");
            } else {
                literalExpr = new IntegerLiteralExpr(String.valueOf(valueDescriptor.getNumber()));
            }
        } else {
            literalExpr = new StringLiteralExpr(opts.getValue());
        }
        return literalExpr;
    }
}
