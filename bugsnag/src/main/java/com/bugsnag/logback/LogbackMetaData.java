package com.bugsnag.logback;

import java.util.ArrayList;
import java.util.List;

/** Used to allow meta data to be added in the logback.xml file */
public class LogbackMetaData {

    private List<LogbackMetaDataTab> tabs = new ArrayList<LogbackMetaDataTab>();

    /**
     * @return The tabs in the meta data
     */
    public List<LogbackMetaDataTab> getTabs() {
        return tabs;
    }

    /**
     * @param tab a new tab to add to the meta data
     */
    public void setTab(LogbackMetaDataTab tab) {
        this.tabs.add(tab);
    }
}
