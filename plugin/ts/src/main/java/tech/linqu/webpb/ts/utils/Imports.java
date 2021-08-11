package tech.linqu.webpb.ts.utils;

import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 * Imports manager.
 */
public class Imports {

    private final Set<String> imported = new TreeSet<>();

    private final String packageName;

    private boolean importWebpb;

    public Imports(String packageName) {
        this.packageName = packageName + ".";
    }

    /**
     * Import Webpb.
     */
    public void importWebpb() {
        this.importWebpb = true;
    }

    /**
     * Check and import a package.
     *
     * @param typeOrPackage type or package to import.
     */
    public void checkAndImport(String typeOrPackage) {
        if (StringUtils.isEmpty(typeOrPackage)) {
            return;
        }
        if (StringUtils.startsWith(typeOrPackage, packageName)) {
            return;
        }
        imported.add(typeOrPackage);
    }

    /**
     * Update a builder by imported.
     *
     * @param builder {@link StringBuilder}
     */
    public void updateBuilder(StringBuilder builder) {
        if (importWebpb) {
            builder.append("import * as Webpb from 'webpb';\n\n");
        }
        for (String type : imported) {
            builder.append("import { ")
                .append(type).append(" } from './").append(type).append("';\n");
        }
        if (!imported.isEmpty()) {
            builder.append('\n');
        }
    }
}
