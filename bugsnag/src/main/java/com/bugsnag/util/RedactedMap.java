package com.bugsnag.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Decorates a map by replacing values of Redacted keys.
 */
public class RedactedMap implements Map<String, Object> {

    private static final String REDACTED_PLACEHOLDER = "[REDACTED]";

    private final Map<String, Object> redactedCopy;
    private final Collection<Pattern> keyRedacts = new ArrayList<>();

    /**
     * Creates a new RedactedMap.
     *
     * @param map        the map to decorate
     * @param keyRedacts a collection of strings where each string is a regex pattern for keys to be redacted
     */
    public RedactedMap(Map<String, Object> map, Collection<String> keyRedacts) {
        for (String redacted : keyRedacts) {
            this.keyRedacts.add(Pattern.compile(redacted, Pattern.CASE_INSENSITIVE));
        }
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
            return new RedactedMap((Map<String, Object>) value, convertPatternsToStrings(keyRedacts));
        }
        return shouldRedactKey((String) key) ? REDACTED_PLACEHOLDER : value;
    }

    private boolean shouldRedactKey(String key) {
        if (keyRedacts == null || key == null) {
            return false;
        }

        for (Pattern redactedPattern : keyRedacts) {
            if (redactedPattern.matcher(key).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a collection of patterns to a collection of strings.
     *
     * @param patterns the patterns to convert
     * @return a collection of strings
     */
    private Collection<String> convertPatternsToStrings(Collection<Pattern> patterns) {
        Collection<String> strings = new ArrayList<>();
        for (Pattern pattern : patterns) {
            strings.add(pattern.pattern());
        }
        return strings;
    }
}
