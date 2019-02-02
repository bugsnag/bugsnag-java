package com.bugsnag.logback;

import java.util.ArrayList;
import java.util.List;

/** Used to allow meta data to be added in the logback.xml file */
public class LogbackMetaDataTab {

    private String name;

    private List<LogbackMetaDataKey> keys = new ArrayList<LogbackMetaDataKey>();

    /**
     * @return The name of the tab
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the tab
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The keys in the tab
     */
    public List<LogbackMetaDataKey> getKeys() {
        return keys;
    }

    /**
     * @param key A key to add to the tab
     */
    public void setKey(LogbackMetaDataKey key) {
        this.keys.add(key);
    }
}
