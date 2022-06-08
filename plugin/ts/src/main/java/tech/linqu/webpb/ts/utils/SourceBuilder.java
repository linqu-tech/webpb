package tech.linqu.webpb.ts.utils;

import java.util.concurrent.Callable;
import tech.linqu.webpb.utilities.utils.Utils;

/**
 * Builder for source code.
 */
public class SourceBuilder {

    private static final String INDENT = "  ";

    private final StringBuilder builder = new StringBuilder();

    private int level = 0;

    /**
     * If is empty.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return builder.length() == 0;
    }

    /**
     * Only leave one newline.
     */
    public void trimDuplicatedNewline() {
        Utils.limitNewline(builder, 1);
    }

    /**
     * Make sure there are count of tail newlines.
     *
     * @param count count
     * @return {@link SourceBuilder}
     */
    public SourceBuilder alignNewline(int count) {
        Utils.alignNewline(this.builder, count);
        return this;
    }

    /**
     * Add close bracket.
     */
    public void closeBracket() {
        trimDuplicatedNewline();
        indent().append("}\n\n");
    }

    /**
     * Wrapper in new indent level.
     *
     * @param runnable {@link Runnable}
     */
    public void level(Runnable runnable) {
        this.level++;
        runnable.run();
        this.level--;
    }

    /**
     * Wrapper in new indent level.
     *
     * @param callable {@link Callable}
     * @param <T>      return type
     * @return value of type T
     */
    public <T> T level(Callable<T> callable) {
        try {
            this.level++;
            T value = callable.call();
            this.level--;
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Indent by level.
     *
     * @return {@link SourceBuilder}
     */
    public SourceBuilder indent() {
        for (int i = 0; i < level; i++) {
            builder.append(INDENT);
        }
        return this;
    }

    /**
     * Append a string.
     *
     * @param value string value
     * @return {@link SourceBuilder}
     */
    public SourceBuilder append(String value) {
        builder.append(value);
        return this;
    }

    /**
     * Append a char.
     *
     * @param value char value
     * @return {@link SourceBuilder}
     */
    public SourceBuilder append(char value) {
        builder.append(value);
        return this;
    }

    /**
     * Append an integer.
     *
     * @param value integer value
     * @return {@link SourceBuilder}
     */
    public SourceBuilder append(int value) {
        builder.append(value);
        return this;
    }

    /**
     * Append an object.
     *
     * @param value object value
     * @return {@link SourceBuilder}
     */
    public SourceBuilder appendObj(Object value) {
        builder.append(value);
        return this;
    }

    /**
     * Prepend a string.
     *
     * @param value string value
     * @return {@link SourceBuilder}
     */
    public SourceBuilder prepend(String value) {
        builder.insert(0, value);
        return this;
    }

    /**
     * Trim last char if match value.
     *
     * @param value char value
     * @return {@link SourceBuilder}
     */
    public SourceBuilder trimLast(char value) {
        if (builder.charAt(builder.length() - 1) == value) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return this;
    }

    /**
     * If {@link SourceBuilder} contains a string.
     *
     * @param value target string
     * @return true if contains the string
     */
    public boolean contains(String value) {
        return builder.indexOf(value) >= 0;
    }

    /**
     * to string.
     *
     * @return string
     */
    @Override
    public String toString() {
        return builder.toString();
    }
}
