package com.bugsnag.logback;

import java.util.ArrayList;
import java.util.List;

/** Configuration to obtain data for additional tabs. */
public class TabConfiguration {
    /** Tab name. */
    private String tab;

    /** Property names. */
    private List<String> properties = new ArrayList<String>();

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public void addProperty(String property) {
        this.properties.add(property);
    }

    public List<String> getProperties() {
        return properties;
    }
}
