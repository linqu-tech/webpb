package tech.linqu.webpb.java.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Name;
import java.util.ArrayList;
import java.util.List;
import tech.linqu.webpb.utilities.utils.Const;

/**
 * Imports manager.
 */
public class Imports {

    private static final JavaParser JAVA_PARSER = new JavaParser();

    private final List<ImportedName> importedNames = new ArrayList<>();

    private final ImportLookup lookup;

    /**
     * Construct an imports manager.
     *
     * @param lookup {@link ImportLookup}
     */
    public Imports(ImportLookup lookup) {
        this.lookup = lookup;
    }

    /**
     * Check an transform to an {@link ImportedName}.
     *
     * @param name {@link Name}
     * @return {@link ImportedName}
     */
    public ImportedName checkAndImport(Name name) {
        return checkAndImport(name.toString());
    }

    /**
     * Check an transform to an {@link ImportedName}.
     *
     * @param name name
     * @return {@link ImportedName}
     */
    public ImportedName checkAndImport(String name) {
        if ("com.google.protobuf.Any".equals(name)) {
            name = Const.RUNTIME_PACKAGE + ".Any";
        }
        ImportedName importedName = findImportedName(importedNames, name);
        if (importedName != null) {
            return importedName;
        }
        ImportedName lookupName = findImportedName(lookup.getNames(), name);
        if (lookupName != null) {
            importedNames.add(lookupName);
            return lookupName;
        }
        return JAVA_PARSER.parseName(name).getResult()
            .map(parsed -> {
                if (!parsed.getQualifier().isPresent()) {
                    return new ImportedName(null, parsed);
                }
                if (lookup.isDuplicated(importedNames, parsed)) {
                    return new ImportedName(null, parsed);
                }
                ImportedName imported = new ImportedName(parsed, new Name(parsed.getIdentifier()));
                importedNames.add(imported);
                return imported;
            })
            .orElse(new ImportedName(null, new Name(name)));
    }

    private ImportedName findImportedName(List<ImportedName> importedNames, String name) {
        for (ImportedName importedName : importedNames) {
            ImportedName resolved = importedName.resolve(name);
            if (resolved != null) {
                return resolved;
            }
        }
        return null;
    }

    /**
     * Update imports of a {@link CompilationUnit}.
     *
     * @param compilationUnit {@link CompilationUnit}
     */
    public void computeUnit(CompilationUnit compilationUnit) {
        importedNames.stream()
            .map(e -> e.getImported().asString())
            .sorted(String::compareTo)
            .forEach(compilationUnit::addImport);
    }
}
