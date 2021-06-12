package tech.linqu.webpb.tests;

import java.io.InputStream;

/**
 * Utilities to handle test dump files.
 */
public enum Dumps {
    TEST1(true),
    TEST2(true),
    TEST3(true),
    TEST4(true),
    TEST5(true),
    TEST6(true),
    TEST7(false);

    private final String dumpName;

    private final boolean valid;

    Dumps(boolean valid) {
        this.dumpName = String.format("/%s/dump/test.dump", this.name().toLowerCase());
        this.valid = valid;
    }

    /**
     * Load dump file.
     */
    public void pipe() {
        InputStream inputStream = getClass().getResourceAsStream(dumpName);
        System.setIn(inputStream);
    }

    /**
     * Is this a valid dump.
     *
     * @return true if valid
     */
    public boolean isValid() {
        return valid;
    }
}
