package com.bugsnag.logback;

/** Used to allow meta data to be added in the logback.xml file */
public class LogbackMetaDataKey {

    private String name;

    private String value;

    /**
     * @return The name of the key
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the key
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The value of the key
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value of the key
     */
    public void setValue(String value) {
        this.value = value;
    }
}
