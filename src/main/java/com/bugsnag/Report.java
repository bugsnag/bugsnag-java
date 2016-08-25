package com.bugsnag;

import com.bugsnag.util.FilterTransformer;
import com.bugsnag.serialization.Expose;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Report {
    private static final String PAYLOAD_VERSION = "2";

    private Configuration config;

    private String apiKey;
    private Throwable throwable;
    private Severity severity = Severity.WARNING;
    private String groupingHash;
    private Diagnostics diagnostics = new Diagnostics();
    private boolean shouldCancel = false;

    Report(Configuration config, Throwable throwable) {
        this.config = config;
        this.throwable = throwable;
    }

    @Expose
    public String getPayloadVersion() {
        return PAYLOAD_VERSION;
    }

    @Expose
    public List<Exception> getExceptions() {
        List<Exception> exceptions = new ArrayList<Exception>();

        Throwable currentThrowable = throwable;
        while (currentThrowable != null) {
            exceptions.add(new Exception(config, currentThrowable));

            currentThrowable = currentThrowable.getCause();
        }

        return exceptions;
    }

    @Expose
    public List<ThreadState> getThreads() {
        return config.sendThreads ? ThreadState.getLiveThreads(config) : null;
    }

    @Expose
    public String getGroupingHash() {
        return groupingHash;
    }

    @Expose
    public String getSeverity() {
        return severity.getValue();
    }

    @Expose
    public String getContext() {
        return diagnostics.context;
    }

    @Expose
    public Map getApp() {
        return diagnostics.app;
    }

    @Expose
    public Map getDevice() {
        return diagnostics.device;
    }

    @Expose
    public Map getUser() {
        return diagnostics.user;
    }

    @Expose
    public Map getMetaData() {
        Map metaDataMap = diagnostics.metaData;

        // Apply filters
        return Maps.transformEntries(metaDataMap, new FilterTransformer(config.filters));
    }

    /**
     * The {@linkplain Throwable exception} which triggered this error report.
     */
    public Throwable getException() {
        return throwable;
    }

    /**
     * Get the class name from the exception contained in this error report.
     */
    public String getExceptionName() {
        return throwable.getClass().getName();
    }

    /**
     * Get the message from the exception contained in this error report.
     */
    public String getExceptionMessage() {
        return throwable.getLocalizedMessage();
    }

    public Report addToTab(String tabName, String key, Object value) {
        diagnostics.metaData.addToTab(tabName, key, value);
        return this;
    }

    public Report clearTab(String tabName) {
        diagnostics.metaData.clearTab(tabName);
        return this;
    }

    public Report setAppInfo(String key, Object value) {
        diagnostics.app.put(key, value);
        return this;
    }

    public Report setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public Report setContext(String context) {
        diagnostics.context = context;
        return this;
    }

    public Report setDeviceInfo(String key, Object value) {
        diagnostics.device.put(key, value);
        return this;
    }

    public Report setGroupingHash(String groupingHash) {
        this.groupingHash = groupingHash;
        return this;
    }

    public Report setSeverity(Severity severity) {
        this.severity = severity;
        return this;
    }

    public Report setUser(String id, String email, String name) {
        diagnostics.user.put("id", id);
        diagnostics.user.put("email", email);
        diagnostics.user.put("name", name);
        return this;
    }

    public Report setUserId(String id) {
        diagnostics.user.put("id", id);
        return this;
    }

    public Report setUserEmail(String email) {
        diagnostics.user.put("email", email);
        return this;
    }

    public Report setUserName(String name) {
        diagnostics.user.put("name", name);
        return this;
    }

    public Report cancel() {
        this.shouldCancel = true;
        return this;
    }

    public boolean getShouldCancel() {
        return this.shouldCancel;
    }
}
