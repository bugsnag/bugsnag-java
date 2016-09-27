package com.bugsnag;

import java.util.HashMap;
import java.util.Map;

class MetaData extends HashMap<String, Object> {
    public void addToTab(String tabName, String key, Object value) {
        Map<String, Object> tab = getTab(tabName);
        tab.put(key, value);
    }

    public void clearTab(String tabName) {
        remove(tabName);
    }

    @SuppressWarnings(value="unchecked")
    private Map<String, Object> getTab(String tabName) {
        Map<String, Object> tab = (Map<String, Object>)get(tabName);
        if (tab == null) {
            tab = new HashMap<String, Object>();
            put(tabName, tab);
        }

        return tab;
    }
}
