package io.github.dawciobiel.shelldialog.cli.dialog;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mutable shared state passed between wizard steps.
 */
public final class WizardContext {

    private final Map<String, Object> values = new LinkedHashMap<>();

    /**
     * Stores a value under the given key.
     *
     * @param key key name
     * @param value value to store
     */
    public void put(String key, Object value) {
        values.put(Objects.requireNonNull(key), value);
    }

    /**
     * Returns the raw value for the supplied key.
     *
     * @param key key name
     * @return stored value
     */
    public Object get(String key) {
        return requireValue(key);
    }

    /**
     * Returns the string value stored under the supplied key.
     *
     * @param key key name
     * @return stored string
     */
    public String getString(String key) {
        return (String) requireValue(key);
    }

    /**
     * Returns the path value stored under the supplied key.
     *
     * @param key key name
     * @return stored path
     */
    public Path getPath(String key) {
        return (Path) requireValue(key);
    }

    /**
     * Returns a snapshot of the current values.
     *
     * @return immutable copy of stored values
     */
    public Map<String, Object> asMap() {
        return Map.copyOf(values);
    }

    private Object requireValue(String key) {
        String normalizedKey = Objects.requireNonNull(key);
        if (!values.containsKey(normalizedKey)) {
            throw new IllegalArgumentException("unknown wizard key: " + normalizedKey);
        }
        return values.get(normalizedKey);
    }
}
