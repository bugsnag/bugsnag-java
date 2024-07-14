package com.bugsnag;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

class MetaData extends HashMap<String, Object> {
    private static final long serialVersionUID = 2530038179702722770L;
    private static Logger logger = Logger.getLogger(MetaData.class.getName());

    public void addToTab(String tabName, String key, Object value) {
        //check if the metadata to add is serializable
        if (!(value instanceof Serializable)) {
            logger.severe("Non-Serializable object attached to metadata: " + key + " in tab: " + tabName);
            // Drop the non-Serializable object and continue
            return;
        }

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
