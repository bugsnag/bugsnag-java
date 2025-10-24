package com.bugsnag;

import java.util.*;

/**
 * Decorates a map by replacing values of redacted keys.
 */
class RedactedMap implements Map<String, Object> {

    private static final String REDACTED_PLACEHOLDER = "[REDACTED]";

    private final Map<String, Object> redactedCopy;
    private final Collection<String> redactedKeys = new HashSet<>();

    public RedactedMap(Map<String, Object> map, Collection<String> redactedKeys) {
        this.redactedKeys.addAll(redactedKeys);
        this.redactedCopy = createCopy(map);
    }

    private Map<String, Object> createCopy(Map<? extends String, ?> map) {
        Map<String, Object> copy = new HashMap<String, Object>();
        for (Entry<? extends String, ?> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                copy.put(entry.getKey(), entry.getValue());
            } else {
                Object transformedValue = transformEntry(entry.getKey(), entry.getValue());
                copy.put(entry.getKey(), transformedValue);
            }
        }
        return copy;
    }

    @Override
    public int size() {
        return redactedCopy.size();
    }

    @Override
    public boolean isEmpty() {
        return redactedCopy.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return redactedCopy.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return redactedCopy.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return redactedCopy.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            return redactedCopy.put(key, null);
        }
        Object transformedValue = transformEntry(key, value);
        return redactedCopy.put(key, transformedValue);
    }

    @Override
    public Object remove(Object key) {
        return redactedCopy.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> mapValues) {
        Map<String, Object> copy = createCopy(mapValues);
        redactedCopy.putAll(copy);
    }

    @Override
    public void clear() {
        redactedCopy.clear();
    }

    @Override
    public Set<String> keySet() {
        return redactedCopy.keySet();
    }

    @Override
    public Collection<Object> values() {
        return redactedCopy.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return redactedCopy.entrySet();
    }

    @SuppressWarnings("unchecked")
    private Object transformEntry(Object key, Object value) {
        if (value instanceof Map) {
            return new RedactedMap((Map<String, Object>) value, redactedKeys);
        }
        return shouldRedactKey((String) key) ? REDACTED_PLACEHOLDER : value;
    }

    private boolean shouldRedactKey(String key) {
        if (key == null) {
            return false;
        }
        //Check for common keys first before looping through all
        if(redactedKeys.contains(key)) {
            return true;
        }
        for (String redactedKey : redactedKeys) {
            if (key.contains(redactedKey)) {
                return true;
            }
        }
        return false;
    }
}
