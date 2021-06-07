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

package tech.linqu.webpb.processor;

import static javax.tools.Diagnostic.Kind.ERROR;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import tech.linqu.webpb.processor.misc.JvmOpens;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.mvc.WebpbRequestMapping;

/**
 * Process the {@link WebpbRequestMapping} and transform it to Spring RequestMapping.
 */
@SupportedAnnotationTypes("tech.linqu.webpb.runtime.mvc.WebpbRequestMapping")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class WebpbRequestMappingProcessor extends AbstractProcessor {

    private static final String SPRING_ANNOTATION = "org.springframework.web.bind.annotation";

    private Trees trees;

    private TreeMaker treeMaker;

    private Names names;

    static {
        JvmOpens.addOpens(WebpbRequestMappingProcessor.class);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        JavacProcessingEnvironment env = getJavacProcessingEnvironment(processingEnv);
        if (env == null) {
            throw new RuntimeException("JavacProcessingEnvironment is required.");
        }
        this.trees = Trees.instance(env);
        this.treeMaker = TreeMaker.instance(env.getContext());
        this.names = Names.instance(env.getContext());
    }

    private JavacProcessingEnvironment getJavacProcessingEnvironment(Object procEnv) {
        if (procEnv instanceof JavacProcessingEnvironment) {
            return (JavacProcessingEnvironment) procEnv;
        }

        logError("Can't get the delegate of the gradle IncrementalProcessingEnvironment.");
        return null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getRootElements()) {
            processUnit(toUnit(element));
        }
        return false;
    }

    private JCCompilationUnit toUnit(Element element) {
        TreePath path = trees.getPath(element);
        return (JCCompilationUnit) path.getCompilationUnit();
    }

    private void processUnit(JCCompilationUnit unit) {
        new TreeScanner() {
            @Override
            public void visitMethodDef(JCMethodDecl tree) {
                tree.mods.annotations = List.from(tree.mods.annotations.stream()
                    .map(annotation -> transformAnnotation(unit, tree, annotation))
                    .collect(Collectors.toList())
                );
                super.visitMethodDef(tree);
            }
        }.scan(unit);
    }

    private JCAnnotation transformAnnotation(JCCompilationUnit unit, JCMethodDecl method,
                                             JCAnnotation annotation) {
        String annotationName = WebpbRequestMapping.class.getSimpleName();
        if (!annotation.annotationType.toString().endsWith(annotationName)) {
            return annotation;
        }
        ArrayList<JCTree.JCExpression> args = new ArrayList<>();
        unit.defs = addImport(unit.defs, SPRING_ANNOTATION, "RequestMapping");
        ClassSymbol messageSymbol = null;
        for (JCTree.JCExpression arg : annotation.args) {
            JCTree.JCAssign assign = (JCTree.JCAssign) arg;
            String argName = (((JCTree.JCIdent) assign.lhs).name).toString();
            if (argName.equals("message")) {
                messageSymbol = assign.rhs.type.allparams().stream()
                    .filter(type -> type.tsym instanceof ClassSymbol)
                    .findFirst().map(type -> (ClassSymbol) type.tsym).orElse(null);
            } else {
                args.add(treeMaker.Assign(treeMaker.Ident(names.fromString(argName)), assign.rhs));
            }
        }
        if (messageSymbol == null) {
            for (JCTree.JCVariableDecl parameter : method.getParameters()) {
                TypeSymbol typeSymbol = parameter.sym.type.tsym;
                for (Type type : ((ClassSymbol) typeSymbol).getInterfaces()) {
                    if (WebpbMessage.class.getName()
                        .equals(type.tsym.getQualifiedName().toString())) {
                        messageSymbol = ((ClassSymbol) typeSymbol);
                        break;
                    }
                }
            }
        }
        if (messageSymbol == null) {
            logError("Should specify a message for WebpbRequestMapping");
            return annotation;
        }
        processSymbolUnit(unit, messageSymbol, args);
        return treeMaker
            .Annotation(treeMaker.Ident(names.fromString("RequestMapping")), List.from(args));
    }

    private void processSymbolUnit(JCCompilationUnit unit, TypeSymbol symbol,
                                   ArrayList<JCTree.JCExpression> args) {
        for (Symbol element : symbol.getEnclosedElements()) {
            if (!(element instanceof Symbol.VarSymbol)) {
                continue;
            }
            Symbol.VarSymbol varSymbol = (Symbol.VarSymbol) element;
            if ("WEBPB_METHOD".equals(varSymbol.getSimpleName().toString())) {
                args.add(treeMaker.Assign(
                    treeMaker.Ident(names.fromString("method")),
                    treeMaker.Select(
                        treeMaker.Ident(names.fromString("RequestMethod")),
                        names.fromString(varSymbol.getConstValue().toString())
                    )
                ));
                unit.defs = addImport(unit.defs, SPRING_ANNOTATION, "RequestMethod");
            }
            if ("WEBPB_PATH".equals(varSymbol.getSimpleName().toString())) {
                String path = varSymbol.getConstValue().toString();
                String mappingPath = path.split("\\?")[0];
                args.add(treeMaker.Assign(
                    treeMaker.Ident(names.fromString("path")),
                    treeMaker.Literal(mappingPath)
                ));
            }
        }
    }

    private List<JCTree> addImport(List<JCTree> trees, String identifier, String name) {
        JCTree.JCImport jcImport = treeMaker.Import(treeMaker.Select(
            treeMaker.Ident(names.fromString(identifier)), names.fromString(name)
        ), false);
        ArrayList<JCTree> jcTrees = new ArrayList<>();
        for (JCTree tree : trees) {
            if (jcImport != null && tree.hasTag(JCTree.Tag.CLASSDEF)) {
                jcTrees.add(jcImport);
                jcTrees.add(tree);
                jcImport = null;
            } else {
                jcTrees.add(tree);
            }
        }
        return List.from(jcTrees);
    }

    private void logError(String error) {
        processingEnv.getMessager().printMessage(ERROR, error);
    }
}
