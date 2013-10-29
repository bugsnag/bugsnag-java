package com.bugsnag;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.bugsnag.utils.JSONUtils;

public class Configuration {
    protected static final String DEFAULT_ENDPOINT = "notify.bugsnag.com";

    private static final String NOTIFIER_NAME = "Java Bugsnag Notifier";
    private static final String NOTIFIER_VERSION = "1.1.0";
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
    String[] notifyReleaseStages = null;
    String[] filters = new String[]{"password"};
    String[] projectPackages;
    String[] ignoreClasses;

    // Error settings
    public String context;
    public String releaseStage;
    public String appVersion;
    public String osVersion;
    MetaData metaData;

    // User settings
    public JSONObject user;

    // Logger
    public Logger logger;

    public Configuration() {
        this.logger = new Logger();
        this.metaData = new MetaData();
        this.user = new JSONObject();
    }

    public String getNotifyEndpoint() {
        return String.format("%s://%s", getProtocol(), endpoint);
    }

    public String getMetricsEndpoint() {
        return String.format("%s://%s/metrics", getProtocol(), endpoint);
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

    public void setUser(String id, String email, String name) {
        JSONUtils.safePutOpt(this.user, "id", id);
        JSONUtils.safePutOpt(this.user, "email", email);
        JSONUtils.safePutOpt(this.user, "name", name);
    }

    public boolean shouldNotify() {
        if(this.notifyReleaseStages == null)
            return true;

        List<String> stages = Arrays.asList(this.notifyReleaseStages);
        return stages.contains(this.releaseStage != null ? this.releaseStage : "production");
    }

    public boolean shouldIgnore(String className) {
        if(this.ignoreClasses == null)
            return false;

        List<String> classes = Arrays.asList(this.ignoreClasses);
        return classes.contains(className);
    }

    private String getProtocol() {
        return (this.useSSL ? "https" : "http");
    }
}