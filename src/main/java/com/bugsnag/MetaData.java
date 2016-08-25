package com.bugsnag;

import java.util.HashMap;
import java.util.Map;

class MetaData extends HashMap {
    public void addToTab(String tabName, String key, Object value) {
        Map tab = getTab(tabName);
        tab.put(key, value);
    }

    public void clearTab(String tabName) {
        remove(tabName);
    }

    private Map getTab(String tabName) {
        Map tab = (Map) get(tabName);
        if (tab == null) {
            tab = new HashMap();
            put(tabName, tab);
        }

        return tab;
    }
}
