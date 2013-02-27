package com.bugsnag;

import java.util.HashMap;
import java.util.Map;

class Configuration {
    private String context;
    private String userId;
    private String releaseStage = "production";
    private String[] notifyReleaseStages = new String[]{"production"};
    private String[] filters = new String[]{"password"};
    private String[] projectPackages;
    private String appVersion;
    private String osVersion;
    private Map<String, Object> metaData;
    private Logger logger;

    public Configuration() {
        this.logger = new Logger();
        this.metaData = new HashMap<String, Object>();
    }

    public Configuration setContext(String context) {
        this.context = context;
        return this;
    }

    public String getContext() {
        return this.context;
    }

    public Configuration setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public Configuration setReleaseStage(String releaseStage) {
        this.releaseStage = releaseStage;
        return this;
    }

    public String getReleaseStage() {
        return this.releaseStage;
    }

    public Configuration setNotifyReleaseStages(String... notifyReleaseStages) {
        this.notifyReleaseStages = notifyReleaseStages;
        return this;
    }

    public String[] getNotifyReleaseStages() {
        return this.notifyReleaseStages;
    }

    public Configuration setFilters(String... filters) {
        this.filters = filters;
        return this;
    }

    public String[] getFilters() {
        return this.filters;
    }

    public Configuration setProjectPackages(String... projectPackages) {
        this.projectPackages = projectPackages;
        return this;
    }

    public String[] getProjectPackages() {
        return this.projectPackages;
    }

    public Configuration setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public Configuration setOsVersion(String osVersion) {
        this.osVersion = osVersion;
        return this;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void addToTab(String tabName, String key, Object value) {
        Object tab = this.metaData.get(tabName);
        if(tab == null || !(tab instanceof Map)) {
            tab = new HashMap<String, Object>();
            this.metaData.put(tabName, tab);
        }

        if(value != null) {
            ((Map)tab).put(key, value);
        } else {
            ((Map)tab).remove(key);
        }
    }
    
    public void clearTab(String tabName){
        this.metaData.remove(tabName);
    }

    public Map<String, Object> getMetaData() {
        return this.metaData;
    }
}