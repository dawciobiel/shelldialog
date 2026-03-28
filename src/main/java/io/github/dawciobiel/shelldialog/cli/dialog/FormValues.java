package io.github.dawciobiel.shelldialog.cli.dialog;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Typed accessor wrapper for values submitted from a {@link FormDialog}.
 */
public final class FormValues {

    private final Map<String, Object> values;

    FormValues(Map<String, Object> values) {
        this.values = Map.copyOf(new LinkedHashMap<>(values));
    }

    /**
     * Returns the raw value stored under the given field name.
     *
     * @param name field name
     * @return stored value
     */
    public Object get(String name) {
        return requireValue(name);
    }

    /**
     * Returns a text field value.
     *
     * @param name field name
     * @return stored string
     */
    public String getString(String name) {
        return (String) requireValue(name);
    }

    /**
     * Returns a password field value.
     *
     * @param name field name
     * @return stored password characters
     */
    public char[] getPassword(String name) {
        return (char[]) requireValue(name);
    }

    /**
     * Returns an immutable copy of all values.
     *
     * @return values keyed by field name
     */
    public Map<String, Object> asMap() {
        return values;
    }

    private Object requireValue(String name) {
        String key = Objects.requireNonNull(name);
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("unknown field name: " + key);
        }
        return values.get(key);
    }
}
