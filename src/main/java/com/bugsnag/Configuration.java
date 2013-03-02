package com.bugsnag;

import java.util.Arrays;
import java.util.List;

class Configuration {
    protected static final String DEFAULT_ENDPOINT = "notify.bugsnag.com";

    private static final String NOTIFIER_NAME = "Java Bugsnag Notifier";
    private static final String NOTIFIER_VERSION = "1.0.1";
    private static final String NOTIFIER_URL = "https://bugsnag.com";

    // Notifier settings
    String notifierName = NOTIFIER_NAME;
    String notifierVersion = NOTIFIER_VERSION;
    String notifierUrl = NOTIFIER_URL;

    // Notification settings
    String apiKey;
    boolean autoNotify = true;
    boolean useSSL = false;
    String endpoint = DEFAULT_ENDPOINT;
    String[] notifyReleaseStages = new String[]{"production"};
    String[] filters = new String[]{"password"};
    String[] projectPackages;

    // Error settings
    String context;
    String userId;
    String releaseStage = "production";
    String appVersion;
    String osVersion;
    MetaData metaData;

    // Logger
    Logger logger;

    public Configuration() {
        this.logger = new Logger();
        this.metaData = new MetaData();
    }

    public String getEndpoint() {
        return (this.useSSL ? "https://" : "http://") + this.endpoint;
    }

    public void addToTab(String tabName, String key, Object value) {
        this.metaData.addToTab(tabName, key, value);
    }
    
    public void clearTab(String tabName){
        this.metaData.clearTab(tabName);
    }

    public MetaData getMetaData() {
        return this.metaData.duplicate();
    }

    public boolean shouldNotify() {
        if(this.notifyReleaseStages == null)
            return false;

        List<String> stages = Arrays.asList(this.notifyReleaseStages);
        return stages.contains(this.releaseStage);
    }
}