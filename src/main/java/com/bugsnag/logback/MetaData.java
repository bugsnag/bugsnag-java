package com.bugsnag.logback;

/** Used to allow meta data to be added in the logback.xml file */
public class MetaData {

    private String tabName;

    private String key;

    private String value;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
