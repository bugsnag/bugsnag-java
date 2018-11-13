package com.bugsnag.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Decorates a map by replacing values of filtered keys.
 */
public class FilteredMap implements Map<String, Object> {

    private static final String FILTERED_PLACEHOLDER = "[FILTERED]";

    private final Map<String, Object> filteredCopy;
    private final Collection<String> keyFilters = new ArrayList<String>();

    public FilteredMap(Map<String, Object> map, Collection<String> keyFilters) {
        this.keyFilters.addAll(keyFilters);
        this.filteredCopy = createCopy(map);
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
        return filteredCopy.size();
    }

    @Override
    public boolean isEmpty() {
        return filteredCopy.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return filteredCopy.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return filteredCopy.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return filteredCopy.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            return filteredCopy.put(key, null);
        }
        Object transformedValue = transformEntry(key, value);
        return filteredCopy.put(key, transformedValue);
    }

    @Override
    public Object remove(Object key) {
        return filteredCopy.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> mapValues) {
        Map<String, Object> copy = createCopy(mapValues);
        filteredCopy.putAll(copy);
    }

    @Override
    public void clear() {
        filteredCopy.clear();
    }

    @Override
    public Set<String> keySet() {
        return filteredCopy.keySet();
    }

    @Override
    public Collection<Object> values() {
        return filteredCopy.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return filteredCopy.entrySet();
    }

    @SuppressWarnings("unchecked")
    private Object transformEntry(Object key, Object value) {
        if (value instanceof Map) {
            return new FilteredMap((Map<String, Object>) value, keyFilters);
        }
        return shouldFilterKey((String) key) ? FILTERED_PLACEHOLDER : value;
    }

    private boolean shouldFilterKey(String key) {
        if (keyFilters == null || key == null) {
            return false;
        }

        for (String filter : keyFilters) {
            if (key.contains(filter)) {
                return true;
            }
        }
        return false;
    }
}
