package com.bugsnag.logback;

import java.util.ArrayList;
import java.util.List;

/** Used to allow metadata to be added in the logback.xml file */
public class LogbackMetadata {

    private List<LogbackMetadataTab> tabs = new ArrayList<LogbackMetadataTab>();

    /**
     * @return The tabs in the metadata
     */
    public List<LogbackMetadataTab> getTabs() {
        return tabs;
    }

    /**
     * @param tab a new tab to add to the metadata
     */
    public void setTab(LogbackMetadataTab tab) {
        this.tabs.add(tab);
    }
}
