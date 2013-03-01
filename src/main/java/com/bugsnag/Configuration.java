package com.bugsnag;

import java.util.Arrays;
import java.util.List;

class Configuration {
    protected static final String DEFAULT_ENDPOINT = "notify.bugsnag.com";

    private String apiKey;
    private boolean autoNotify = true;
    private boolean useSSL = false;
    private String endpoint = DEFAULT_ENDPOINT;
    private String context;
    private String userId;
    private String releaseStage = "production";
    private String[] notifyReleaseStages = new String[]{"production"};
    private String[] filters = new String[]{"password"};
    private String[] projectPackages;
    private String appVersion;
    private String osVersion;
    private MetaData metaData;
    private Logger logger;

    public Configuration() {
        this.logger = new Logger();
        this.metaData = new MetaData();
    }

    public Configuration setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public Configuration setAutoNotify(boolean autoNotify) {
        this.autoNotify = autoNotify;
        return this;
    }

    public boolean getAutoNotify() {
        return this.autoNotify;
    }

    public Configuration setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
        return this;
    }

    public boolean getUseSSL() {
        return this.useSSL;
    }

    public Configuration setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getEndpoint() {
        return (this.useSSL ? "https://" : "http://") + this.endpoint;
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

    public Configuration setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public Logger getLogger() {
        return this.logger;
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