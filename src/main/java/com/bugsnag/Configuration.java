package com.bugsnag;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.callbacks.AppCallback;
import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.callbacks.ServletCallback;
import com.bugsnag.delivery.AsyncHttpDelivery;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Configuration {

    private String apiKey;
    private String appType;
    private String appVersion;
    private Delivery delivery = new AsyncHttpDelivery();
    private String[] filters = new String[]{"password"};
    private String[] ignoreClasses;
    private String[] notifyReleaseStages = null;
    private String[] projectPackages;
    private String releaseStage;
    private boolean sendThreads = false;

    Collection<Callback> callbacks = new ArrayList<Callback>();
    Serializer serializer = new Serializer();

    Configuration(String apiKey) {
        this.apiKey = apiKey;

        // Add built-in callbacks
        addCallback(new AppCallback(this));
        addCallback(new DeviceCallback());

        if (ServletCallback.isAvailable()) {
            addCallback(new ServletCallback());
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String[] getFilters() {
        return filters;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
    }

    public String[] getIgnoreClasses() {
        return ignoreClasses;
    }

    public void setIgnoreClasses(String[] ignoreClasses) {
        this.ignoreClasses = ignoreClasses;
    }

    public String[] getNotifyReleaseStages() {
        return notifyReleaseStages;
    }

    public void setNotifyReleaseStages(String[] notifyReleaseStages) {
        this.notifyReleaseStages = notifyReleaseStages;
    }

    public String[] getProjectPackages() {
        return projectPackages;
    }

    public void setProjectPackages(String[] projectPackages) {
        this.projectPackages = projectPackages;
    }

    public String getReleaseStage() {
        return releaseStage;
    }

    public void setReleaseStage(String releaseStage) {
        this.releaseStage = releaseStage;
    }

    public boolean isSendThreads() {
        return sendThreads;
    }

    public void setSendThreads(boolean sendThreads) {
        this.sendThreads = sendThreads;
    }

    public Delivery getDelivery() {
        return this.delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    protected boolean shouldNotifyForReleaseStage() {
        if (notifyReleaseStages == null) {
            return true;
        }

        List<String> stages = Arrays.asList(notifyReleaseStages);
        return stages.contains(releaseStage);
    }

    protected boolean shouldIgnoreClass(String className) {
        if (ignoreClasses == null) {
            return false;
        }

        List<String> classes = Arrays.asList(ignoreClasses);
        return classes.contains(className);
    }

    protected void addCallback(Callback callback) {
        callbacks.add(callback);
    }

    protected boolean inProject(String className) {
        if (projectPackages != null) {
            for (String packageName : projectPackages) {
                if (packageName != null && className.startsWith(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
