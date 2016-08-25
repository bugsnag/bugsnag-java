package com.bugsnag.util;

import com.google.common.collect.Maps;

import java.util.Map;

public class FilterTransformer implements Maps.EntryTransformer<Object, Object, Object> {
    private static final String FILTERED_PLACEHOLDER = "[FILTERED]";

    private String[] keyFilters;
    private boolean deep = true;

    public FilterTransformer(String... keyFilters) {
        this.keyFilters = keyFilters;
    }

    @Override
    public Object transformEntry(Object key, Object value) {
        if (deep && value instanceof Map) {
            return Maps.transformEntries((Map)value, this);
        }

        return shouldFilterKey(key) ? FILTERED_PLACEHOLDER : value;
    }

    private boolean shouldFilterKey(Object key) {
        if (keyFilters == null || key == null || !(key instanceof String)) {
            return false;
        }

        for (String filter : keyFilters) {
            if (((String)key).contains(filter)) {
                return true;
            }
        }

        return false;
    }
}
