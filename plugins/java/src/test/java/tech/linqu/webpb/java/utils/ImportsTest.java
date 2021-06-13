package tech.linqu.webpb.java.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.javaparser.ast.expr.Name;
import org.junit.jupiter.api.Test;
import tech.linqu.webpb.utilities.utils.Const;

class ImportsTest {

    @Test
    void shouldTestCheckAndImportSuccess() {
        Imports imports = new Imports(new ImportLookup());
        Name source1 = new Name(new Name("a.b"), "c");
        ImportedName name1 = imports.checkAndImport(source1);
        assertEquals("c", name1.getName().asString());
        assertEquals("a.b.c", name1.getImported().asString());

        ImportedName name2 = imports.checkAndImport(source1);
        assertEquals("c", name2.getName().asString());
        assertEquals("a.b.c", name2.getImported().asString());

        Name source2 = new Name(new Name("d.e"), "c");
        ImportedName name3 = imports.checkAndImport(source2);
        assertEquals("d.e.c", name3.getName().asString());
        assertNull(name3.getImported());

        Name source3 = new Name(new Name("f.g"), "c.h");
        ImportedName name4 = imports.checkAndImport(source3);
        assertEquals("h", name4.getName().asString());
        assertEquals("f.g.c.h", name4.getImported().asString());

        Name source4 = new Name(new Name("i.j"), "k.l");
        ImportedName name5 = imports.checkAndImport(source4);
        assertEquals("l", name5.getName().asString());
        assertEquals("i.j.k.l", name5.getImported().asString());

        Name source5 = new Name("m.n");
        ImportedName name6 = imports.checkAndImport(source5);
        assertEquals("n", name6.getName().asString());
        assertEquals("m.n", name6.getImported().asString());

        Name source6 = new Name("o");
        ImportedName name7 = imports.checkAndImport(source6);
        assertEquals("o", name7.getName().asString());
        assertNull(name7.getImported());
    }

    @Test
    void shouldCheckAndImportChangeQualifier() {
        Imports imports = new Imports(new ImportLookup());
        ImportedName name1 = imports.checkAndImport(Const.RUNTIME_PACKAGE + ".Any");
        assertEquals(Const.RUNTIME_PACKAGE + "." + "Any", name1.getImported().asString());
    }
}
