package tech.linqu.webpb.java.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ImportedNameTest {

    @Test
    void shouldTestConstructSuccess() {
        ImportedName importedName = new ImportedName("a.b.c");
        assertEquals("a.b.c", importedName.getImported().asString());
        assertEquals("c", importedName.getName().asString());

        assertThrows(RuntimeException.class, () -> new ImportedName("a..b"), "Invalid name: a..b");
    }

    @Test
    void shouldResolveSuccess() {
        ImportedName importedName1 = new ImportedName("a.b.c");

        ImportedName resolved1 = importedName1.resolve("c");
        assertEquals("a.b.c", resolved1.getImported().asString());
        assertEquals("c", resolved1.getName().asString());

        ImportedName resolved2 = importedName1.resolve("c.d");
        assertEquals("a.b.c", resolved2.getImported().asString());
        assertEquals("c.d", resolved2.getName().asString());

        ImportedName resolved3 = importedName1.resolve("b.c.d");
        assertEquals("a.b.c", resolved3.getImported().asString());
        assertEquals("c.d", resolved3.getName().asString());

        assertNull(importedName1.resolve("e.c.d"));
        assertNull(importedName1.resolve("e.d"));
        assertNull(importedName1.resolve("c\\"));
        assertNull(importedName1.resolve("c..d"));

        ImportedName importedName2 = new ImportedName("a.b");
        ImportedName resolved4 = importedName2.resolve("b.c");
        assertEquals("a.b", resolved4.getImported().asString());
        assertEquals("b.c", resolved4.getName().asString());
    }
}
