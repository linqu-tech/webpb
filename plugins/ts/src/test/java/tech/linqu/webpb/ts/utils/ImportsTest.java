package tech.linqu.webpb.ts.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ImportsTest {

    @Test
    void shouldCheckAndImportSuccess() {
        Imports imports = new Imports("a");
        StringBuilder builder = new StringBuilder();

        imports.checkAndImport(null);
        imports.updateBuilder(builder);
        assertEquals(0, builder.length());

        imports.checkAndImport("a.b");
        imports.updateBuilder(builder);
        assertEquals(0, builder.length());
    }
}
