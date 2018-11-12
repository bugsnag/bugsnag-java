package com.bugsnag;

import java.util.HashMap;
import java.util.Map;

class MetaData extends HashMap<String, Object> {
    private static final long serialVersionUID = 2530038179702722770L;

    public void addToTab(String tabName, String key, Object value) {
        Map<String, Object> tab = getTab(tabName);
        tab.put(key, value);
    }

    void clearTab(String tabName) {
        remove(tabName);
    }

    void clearKey(String tabName, String key) {
        Map<String, Object> tab = getTab(tabName);
        tab.remove(key);
    }

    void merge(MetaData metaData) {
        for (String tabName : metaData.keySet()) {
            getTab(tabName).putAll(metaData.getTab(tabName));
        }
    }

    @SuppressWarnings(value = "unchecked")
    private Map<String, Object> getTab(String tabName) {
        Map<String, Object> tab = (Map<String, Object>) get(tabName);
        if (tab == null) {
            tab = new HashMap<String, Object>();
            put(tabName, tab);
        }

        return tab;
    }
}
