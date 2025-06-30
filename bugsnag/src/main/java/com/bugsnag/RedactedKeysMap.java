package com.bugsnag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Decorates a map by replacing values of redacted keys.
 */
class RedactedKeysMap implements Map<String, Object> {

    private static final String REDACTED_PLACEHOLDER = "[REDACTED]";

    private final Map<String, Object> redactedKeyCopy;
    private final Pattern[] redactedKeys;
    private final String[] filters;

    /**
     * Constructs a new RedactedKeysMap by copying the provided map and applying
     * redaction rules to the specified keys.
     *
     * @param map           the original map to be wrapped and redacted
     * @param configuration the configuration
     */
    RedactedKeysMap(Map<String, Object> map, Configuration configuration) {
        this(map, configuration.redactedKeys, configuration.filters);
    }

    RedactedKeysMap(Map<String, Object> map, Pattern[] redactedKeys, String[] filters) {
        this.filters = filters;
        this.redactedKeys = redactedKeys;
        this.redactedKeyCopy = createCopy(map);
    }

    /**
     * Creates a copy of the given map, applying redaction rules to the specified keys.
     *
     * @param map the original map to be copied and redacted
     * @return a new map with the same entries as the original, but with redacted values where applicable
     */
    private Map<String, Object> createCopy(Map<? extends String, ?> map) {
        Map<String, Object> copy = new HashMap<>();
        for (Entry<? extends String, ?> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                copy.put(entry.getKey(), null);
            } else {
                Object transformedValue = transformEntry(entry.getKey(), entry.getValue());
                copy.put(entry.getKey(), transformedValue);
            }
        }
        return copy;
    }

    @Override
    public int size() {
        return redactedKeyCopy.size();
    }

    @Override
    public boolean isEmpty() {
        return redactedKeyCopy.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return redactedKeyCopy.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return redactedKeyCopy.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return redactedKeyCopy.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            return redactedKeyCopy.put(key, null);
        }
        Object transformedValue = transformEntry(key, value);
        return redactedKeyCopy.put(key, transformedValue);
    }

    @Override
    public Object remove(Object key) {
        return redactedKeyCopy.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> mapValues) {
        Map<String, Object> copy = createCopy(mapValues);
        redactedKeyCopy.putAll(copy);
    }

    @Override
    public void clear() {
        redactedKeyCopy.clear();
    }

    @Override
    public Set<String> keySet() {
        return redactedKeyCopy.keySet();
    }

    @Override
    public Collection<Object> values() {
        return redactedKeyCopy.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return redactedKeyCopy.entrySet();
    }

    @SuppressWarnings("unchecked")
    private Object transformEntry(Object key, Object value) {
        if (value instanceof Map) {
            return new RedactedKeysMap((Map<String, Object>) value, redactedKeys, filters);
        }
        return shouldRedactKey((String) key) ? REDACTED_PLACEHOLDER : value;
    }

    private boolean shouldRedactKey(String key) {
        if (key == null) {
            return false;
        }

        Pattern[] redactedKeys = this.redactedKeys;
        if (redactedKeys != null) {
            for (Pattern pattern : redactedKeys) {
                if (pattern.matcher(key).matches()) {
                    return true;
                }
            }
        }

        String[] filters = this.filters;
        if (filters != null) {
            for (String filter : filters) {
                if (filter != null && key.contains(filter)) {
                    return true;
                }
            }
        }

        return false;
    }
}
