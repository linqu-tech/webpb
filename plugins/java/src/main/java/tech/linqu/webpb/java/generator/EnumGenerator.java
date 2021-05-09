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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.google.protobuf.Descriptors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor(staticName = "create")
public class EnumGenerator {

    private static final String ENUM_VALUE = "value";

    public EnumDeclaration generate(Descriptors.EnumDescriptor descriptor) {
        EnumDeclaration declaration = new EnumDeclaration();
        declaration.setName(descriptor.getName());
        declaration.addModifier(Modifier.Keyword.PUBLIC);

        for (Descriptors.EnumValueDescriptor valueDescriptor : descriptor.getValues()) {
            EnumConstantDeclaration enumConstant = declaration.addEnumConstant(valueDescriptor.getName());
            enumConstant.addArgument(new IntegerLiteralExpr(String.valueOf(valueDescriptor.getIndex())));
        }

        declaration.addField(PrimitiveType.intType(), ENUM_VALUE, Modifier.Keyword.PRIVATE);
        generateEnumConstructor(declaration);
        generateEnumOfMethod(declaration, descriptor);
        generateEnumValueGetter(declaration);
        return declaration;
    }

    private void generateEnumConstructor(EnumDeclaration declaration) {
        ConstructorDeclaration constructor = declaration.addConstructor();
        constructor.addParameter(new Parameter(PrimitiveType.intType(), ENUM_VALUE));
        constructor.getBody().addStatement(new AssignExpr(
            new FieldAccessExpr(new ThisExpr(), ENUM_VALUE),
            new NameExpr(ENUM_VALUE),
            AssignExpr.Operator.ASSIGN
        ));
    }

    private void generateEnumOfMethod(EnumDeclaration declaration, Descriptors.EnumDescriptor descriptor) {
        MethodDeclaration method = declaration.addMethod("of", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        method.addParameter(new Parameter(PrimitiveType.intType(), ENUM_VALUE));
        method.setType(declaration.getName().asString());
        NodeList<SwitchEntry> entries = new NodeList<>();
        for (Descriptors.EnumValueDescriptor valueDescriptor : descriptor.getValues()) {
            entries.add(new SwitchEntry(
                NodeList.nodeList(new IntegerLiteralExpr(String.valueOf(valueDescriptor.getIndex()))),
                SwitchEntry.Type.STATEMENT_GROUP,
                NodeList.nodeList(new ReturnStmt(new NameExpr(valueDescriptor.getName())))
            ));
        }
        entries.add(new SwitchEntry().addStatement(new ReturnStmt("null")));
        method.setBody(new BlockStmt().addStatement(new SwitchStmt(new NameExpr(ENUM_VALUE), entries)));
    }

    private void generateEnumValueGetter(EnumDeclaration declaration) {
        MethodDeclaration method = declaration.addMethod("get" + StringUtils.capitalize(ENUM_VALUE), Modifier.Keyword.PUBLIC);
        method.setType(PrimitiveType.intType());
        method.setBody(new BlockStmt().addStatement(new ReturnStmt(
            new FieldAccessExpr(new ThisExpr(), ENUM_VALUE)
        )));
    }
}
