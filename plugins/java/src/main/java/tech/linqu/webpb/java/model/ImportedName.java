package tech.linqu.webpb.java.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link Name} with imported info.
 */
public class ImportedName {

    private static final JavaParser JAVA_PARSER = new JavaParser();

    private final Name imported;

    private final Name name;

    /**
     * Construct an {@link ImportedName}.
     *
     * @param imported name to import
     */
    public ImportedName(String imported) {
        Optional<Name> optional = JAVA_PARSER.parseName(imported).getResult();
        if (!optional.isPresent()) {
            throw new RuntimeException("Invalid name: " + imported);
        }
        this.imported = optional.get();
        this.name = new Name(optional.get().getIdentifier());
    }

    /**
     * Construct an {@link ImportedName}.
     *
     * @param imported {@link Name}
     * @param name     {@link Name}
     */
    public ImportedName(Name imported, Name name) {
        this.imported = imported;
        this.name = name;
    }

    /**
     * Resolve an {@link ImportedName} if {@link Name} is imported by this.
     *
     * @param name {@link Name} to resolve
     * @return {@link ImportedName}
     */
    public ImportedName resolve(String name) {
        if (this.name.asString().equals(name)) {
            return new ImportedName(imported, this.name);
        }
        Optional<Expression> optional = JAVA_PARSER.parseExpression(name).getResult();
        if (!optional.isPresent() || !(optional.get() instanceof FieldAccessExpr)) {
            return null;
        }
        FieldAccessExpr expr = optional.get().asFieldAccessExpr();
        List<SimpleName> nameList = new ArrayList<>();
        if (!match(expr, nameList)) {
            return null;
        }
        Collections.reverse(nameList);
        String n = nameList.stream().map(SimpleName::asString).collect(Collectors.joining("."));
        return new ImportedName(imported, new Name(n));
    }

    private boolean match(FieldAccessExpr expr, List<SimpleName> nameList) {
        nameList.add(expr.getName());
        if (name.asString().equals(expr.getNameAsString())) {
            return imported.toString().endsWith(expr.toString());
        }
        if (expr.getScope() instanceof FieldAccessExpr) {
            return match((FieldAccessExpr) expr.getScope(), nameList);
        }
        NameExpr nameExpr = (NameExpr) expr.getScope();
        if (name.asString().equals(nameExpr.getName().toString())) {
            nameList.add(nameExpr.getName());
            return true;
        }
        return false;
    }

    /**
     * Imported {@link Name}.
     *
     * @return {@link Name}
     */
    public Name getImported() {
        return imported;
    }

    /**
     * Refer {@link Name}.
     *
     * @return {@link Name}
     */
    public Name getName() {
        return name;
    }
}
