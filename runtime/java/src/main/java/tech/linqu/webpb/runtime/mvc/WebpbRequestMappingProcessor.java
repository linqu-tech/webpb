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
package tech.linqu.webpb.runtime.mvc;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import tech.linqu.webpb.runtime.utils.JvmOpens;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sun.source.tree.Tree.Kind.ASSIGNMENT;
import static com.sun.source.tree.Tree.Kind.IDENTIFIER;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes("tech.linqu.webpb.runtime.mvc.WebpbRequestMapping")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class WebpbRequestMappingProcessor extends AbstractProcessor {

    private Trees trees;

    private TreeMaker treeMaker;

    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        JavacProcessingEnvironment env = getJavacProcessingEnvironment(processingEnv);
        this.trees = Trees.instance(env);
        this.treeMaker = TreeMaker.instance(env.getContext());
        this.names = Names.instance(env.getContext());
    }

    public JavacProcessingEnvironment getJavacProcessingEnvironment(Object procEnv) {
        JvmOpens.addOpens(this.getClass());
        if (procEnv instanceof JavacProcessingEnvironment) {
            return (JavacProcessingEnvironment) procEnv;
        }

        processingEnv.getMessager().printMessage(ERROR,
            "Can't get the delegate of the gradle IncrementalProcessingEnvironment.");
        return null;
    }

    private JCCompilationUnit toUnit(Element element) {
        TreePath path = null;
        if (trees != null) {
            try {
                path = trees.getPath(element);
            } catch (NullPointerException ignore) {
            }
        }
        if (path == null) {
            return null;
        }
        return (JCCompilationUnit) path.getCompilationUnit();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ClassSymbol> classSymbolMap = roundEnv.getRootElements().stream()
            .filter((Predicate<Element>) element -> element instanceof ClassSymbol)
            .collect(Collectors.toMap(
                element -> ((ClassSymbol) element).fullname.toString(),
                element -> (ClassSymbol) element
            ));
        for (ClassSymbol classSymbol : classSymbolMap.values()) {
            JCCompilationUnit unit = toUnit(classSymbol);
            if (unit == null) {
                continue;
            }
            processUnit(unit, classSymbolMap);
        }
        return true;
    }

    private void processUnit(JCCompilationUnit unit, Map<String, ClassSymbol> classSymbolMap) {
        new TreeScanner() {
            @Override
            public void visitMethodDef(JCMethodDecl tree) {
                tree.mods.annotations = List.from(tree.mods.annotations.stream().map(annotation -> {
                    String annotationName = WebpbRequestMapping.class.getSimpleName();
                    if (!annotation.annotationType.toString().endsWith(annotationName)) {
                        return annotation;
                    }
                    ArrayList<JCTree.JCExpression> args = new ArrayList<>();
                    for (JCTree.JCExpression arg : annotation.args) {
                        if (!arg.getKind().equals(ASSIGNMENT)) {
                            continue;
                        }
                        JCTree.JCAssign assign = (JCTree.JCAssign) arg;
                        if (!assign.lhs.getKind().equals(IDENTIFIER)) {
                            continue;
                        }
                        String argName = (((JCTree.JCIdent) assign.lhs).name).toString();
                        if (!argName.equals("message")) {
                            args.add(treeMaker.Assign(treeMaker.Ident(names.fromString(argName)), assign.rhs));
                            continue;
                        }
                        unit.defs = addImport(unit.defs, "org.springframework.web.bind.annotation", "RequestMapping");
                        Type type = assign.rhs.type.allparams().stream().findFirst().orElse(null);
                        if (type == null) {
                            continue;
                        }
                        ClassSymbol symbol = (ClassSymbol) type.tsym;
                        if (symbol == null) {
                            processingEnv.getMessager().printMessage(ERROR, "Cannot find class symbol: " + type);
                            continue;
                        }
                        processSymbolUnit(unit, symbol, args);
                    }
                    return treeMaker.Annotation(
                        treeMaker.Ident(names.fromString("RequestMapping")), List.from(args)
                    );
                }).collect(Collectors.toList()));
                super.visitMethodDef(tree);
            }
        }.scan(unit);
    }

    private void processSymbolUnit(JCCompilationUnit unit, ClassSymbol symbol, ArrayList<JCTree.JCExpression> args) {
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
                unit.defs = addImport(unit.defs, "org.springframework.web.bind.annotation", "RequestMethod");
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
            if (jcImport != null && tree.hasTag(JCTree.Tag.PACKAGEDEF)) {
                jcTrees.add(tree);
                jcTrees.add(jcImport);
                jcImport = null;
            } else {
                jcTrees.add(tree);
            }
        }
        return List.from(jcTrees);
    }
}
