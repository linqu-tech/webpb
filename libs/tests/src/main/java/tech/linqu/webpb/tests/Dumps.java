package tech.linqu.webpb.tests;

import java.io.InputStream;

/**
 * Utilities to handle test dump files.
 */
public enum Dumps {
    TEST1("/test1/dump/test.dump"),

    TEST2("/test2/dump/test.dump"),

    TEST3("/test3/dump/test.dump");

    private final String dumpName;

    Dumps(String dumpName) {
        this.dumpName = dumpName;
    }

    /**
     * Load dump file.
     */
    public void pipe() {
        InputStream inputStream = getClass().getResourceAsStream(dumpName);
        System.setIn(inputStream);
    }
}
