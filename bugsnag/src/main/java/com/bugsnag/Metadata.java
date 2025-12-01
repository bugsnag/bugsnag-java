package com.bugsnag;

import java.util.HashMap;
import java.util.Map;

class Metadata extends HashMap<String, Object> {
    private static final long serialVersionUID = 2530038179702722770L;

    public void addMetadata(String tabName, String key, Object value) {
        Map<String, Object> tab = getMetadata(tabName);
        tab.put(key, value);
    }

    void clearMetadata(String tabName) {
        remove(tabName);
    }

    void clearMetadata(String tabName, String key) {
        Map<String, Object> tab = getMetadata(tabName);
        tab.remove(key);
    }

    void merge(Metadata metadata) {
        for (String tabName : metadata.keySet()) {
            getMetadata(tabName).putAll(metadata.getMetadata(tabName));
        }
    }

    @SuppressWarnings(value = "unchecked")
    private Map<String, Object> getMetadata(String tabName) {
        Map<String, Object> tab = (Map<String, Object>) get(tabName);
        if (tab == null) {
            tab = new HashMap<String, Object>();
            put(tabName, tab);
        }

        return tab;
    }
}
