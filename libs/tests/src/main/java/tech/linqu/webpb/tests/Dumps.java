package tech.linqu.webpb.tests;

import java.io.InputStream;

/**
 * Utilities to handle test dump files.
 */
public enum Dumps {
    TEST1,
    TEST2,
    TEST3;

    private final String dumpName;

    Dumps() {
        this.dumpName = String.format("/%s/dump/test.dump", this.name().toLowerCase());
    }

    /**
     * Load dump file.
     */
    public void pipe() {
        InputStream inputStream = getClass().getResourceAsStream(dumpName);
        System.setIn(inputStream);
    }
}
