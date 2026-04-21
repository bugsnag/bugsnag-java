package com.bugsnag.logback;

/**
 * Used to allow feature flags to be configured in the logback.xml file.
 */
public class LogbackFeatureFlag {

    private String name;
    private String variant;

    /**
     * @return the name of the feature flag
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the feature flag
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the variant of the feature flag
     */
    public String getVariant() {
        return variant;
    }

    /**
     * @param variant the variant of the feature flag
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }
}
