package tech.linqu.webpb.java.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Name;
import com.google.protobuf.Descriptors.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.FileOpts;
import tech.linqu.webpb.utilities.descriptor.WebpbExtend.JavaFileOpts;
import tech.linqu.webpb.utilities.utils.OptionUtils;

/**
 * Lookup imports defined in {@link FileOpts}.
 */
public class ImportLookup {

    private static final JavaParser JAVA_PARSER = new JavaParser();

    private final List<ImportedName> names = new ArrayList<>();

    /**
     * Copy a {@link ImportLookup}.
     *
     * @param lookup {@link ImportLookup}
     * @return Copied {@link ImportLookup}
     */
    public ImportLookup copy(ImportLookup lookup) {
        this.names.addAll(lookup.names);
        return this;
    }

    /**
     * Update a {@link ImportLookup}.
     *
     * @param fileDescriptor {@link FileDescriptor}
     * @return {@link ImportLookup}
     */
    public ImportLookup update(FileDescriptor fileDescriptor) {
        JavaFileOpts fileOpts = OptionUtils.getOpts(fileDescriptor, FileOpts::hasJava).getJava();
        for (String qualifiedName : fileOpts.getImportList()) {
            JAVA_PARSER.parseName(qualifiedName).getResult()
                .map(name -> {
                    if (isDuplicated(names, name)) {
                        throw new RuntimeException("Duplicated import: " + qualifiedName);
                    }
                    names.add(new ImportedName(name, new Name(name.getIdentifier())));
                    return name;
                })
                .orElseThrow(() -> new RuntimeException("Bad import: " + qualifiedName));
        }
        return this;
    }

    /**
     * Check if {@link Name} to imported is duplicated.
     *
     * @param importedNames list of {@link ImportedName} to check
     * @param name          {@link Name} to check
     * @return true if duplicated
     */
    public boolean isDuplicated(List<ImportedName> importedNames, Name name) {
        for (ImportedName imported : importedNames) {
            if (imported.getName().toString().equals(name.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    public List<ImportedName> getNames() {
        return names;
    }
}
