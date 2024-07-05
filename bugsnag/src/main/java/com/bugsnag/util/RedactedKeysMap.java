package com.bugsnag.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Decorates a map by replacing values of redacted keys.
 */
public class RedactedKeysMap implements Map<String, Object> {

    private static final String REDACTED_PLACEHOLDER = "[REDACTED]";

    private final Map<String, Object> redactedCopy;
    private final Collection<Pattern> keyRedacts = new ArrayList<>();

    /**
     * Constructs a new RedactedKeysMap with the specified map and key.
     * RedactedKeys can be plain strings or regex patterns. The key matching is case-insensitive.
     *
     * @param map        the original map to be decorated with redacted values
     * @param keyRedacts a collection of strings representing keys to be redacted.
     *                   Plain strings and regex patterns are supported.
     */
    public RedactedKeysMap(Map<String, Object> map, Collection<String> keyRedacts) {
        for (String key : keyRedacts) {
            if (isRegexPattern(key)) {
                this.keyRedacts.add(Pattern.compile(key, Pattern.CASE_INSENSITIVE));
            } else {
                this.keyRedacts.add(Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE));
            }
        }
        this.redactedCopy = createCopy(map);
    }

    private boolean isRegexPattern(String key) {
        return key.matches(".*[.\\*\\+\\?\\^\\$\\[\\]\\(\\)\\{\\}\\|\\\\].*");
    }

    private Map<String, Object> createCopy(Map<? extends String, ?> map) {
        Map<String, Object> copy = new HashMap<>();
        for (Map.Entry<? extends String, ?> entry : map.entrySet()) {
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
    public Set<Map.Entry<String, Object>> entrySet() {
        return redactedCopy.entrySet();
    }

    @SuppressWarnings("unchecked")
    private Object transformEntry(String key, Object value) {
        if (value instanceof Map) {
            return new RedactedKeysMap((Map<String, Object>) value, extractRedactedPatterns());
        }
        return shouldRedactKey(key) ? REDACTED_PLACEHOLDER : value;
    }

    private Collection<String> extractRedactedPatterns() {
        Collection<String> patterns = new ArrayList<>();
        for (Pattern pattern : keyRedacts) {
            patterns.add(pattern.pattern());
        }
        return patterns;
    }

    private boolean shouldRedactKey(String key) {
        if (keyRedacts == null || key == null) {
            return false;
        }

        for (Pattern redactPattern : keyRedacts) {
            if (redactPattern.matcher(key).find()) {
                return true;
            }
        }
        return false;
    }
}
