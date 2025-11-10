package com.bugsnag.logback;

import java.util.ArrayList;
import java.util.List;

/** Used to allow metadata to be added in the logback.xml file */
public class LogbackMetadataTab {

    private String name;

    private List<LogbackMetadataKey> keys = new ArrayList<LogbackMetadataKey>();

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
    public List<LogbackMetadataKey> getKeys() {
        return keys;
    }

    /**
     * @param key A key to add to the tab
     */
    public void setKey(LogbackMetadataKey key) {
        this.keys.add(key);
    }
}
