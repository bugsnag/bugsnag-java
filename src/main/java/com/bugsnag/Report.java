package com.bugsnag;

import com.bugsnag.serialization.Expose;
import com.bugsnag.util.FilterTransformer;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Report {
    private static final String PAYLOAD_VERSION = "3";

    private Configuration config;

    private String apiKey;
    private Throwable throwable;
    private final HandledState handledState;
    private Severity severity;
    private String groupingHash;
    private Diagnostics diagnostics = new Diagnostics();
    private boolean shouldCancel = false;

    /**
     * Create a report for the error.
     *
     * @param config    the configuration for the report.
     * @param throwable the error to create the report for.
     */
    protected Report(Configuration config, Throwable throwable) {
        this(config, throwable, new HandledState(Severity.WARNING, null, null));
    }

    Report(Configuration config, Throwable throwable, HandledState handledState) {
        this.config = config;
        this.throwable = throwable;
        this.handledState = handledState;
        this.severity = handledState.getOriginalSeverity();
    }

    @Expose
    protected String getPayloadVersion() {
        return PAYLOAD_VERSION;
    }

    /**
     * Get the exceptions for the report.
     *
     * @return the exceptions that make up the error.
     */
    @Expose
    protected List<Exception> getExceptions() {
        List<Exception> exceptions = new ArrayList<Exception>();

        Throwable currentThrowable = throwable;
        while (currentThrowable != null) {
            exceptions.add(new Exception(config, currentThrowable));
            currentThrowable = currentThrowable.getCause();
        }

        return exceptions;
    }

    @Expose
    boolean getDefaultSeverity() {
        return handledState.isDefaultSeverity(severity);
    }

    @Expose
    boolean getUnhandled() {
        return handledState.isUnhandled();
    }

    @Expose
    SeverityReason getSeverityReason() {
        HandledState.SeverityReasonType severityReasonType = handledState.getSeverityReasonType();
        if (severityReasonType != null) {
            return new SeverityReason(handledState.getSeverityReasonType().toString());
        } else {
            return null;
        }
    }

    @Expose
    protected List<ThreadState> getThreads() {
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
        Map<String, Object> metaDataMap = diagnostics.metaData;
        return Maps.transformEntries(metaDataMap, new FilterTransformer(config.filters));
    }

    /**
     * @return The {@linkplain Throwable exception} which triggered this error report.
     */
    public Throwable getException() {
        return throwable;
    }

    /**
     * @return the class name from the exception contained in this error report.
     */
    public String getExceptionName() {
        return throwable.getClass().getName();
    }

    /**
     * @return The message from the exception contained in this error report.
     */
    public String getExceptionMessage() {
        return throwable.getLocalizedMessage();
    }

    /**
     * Add a key value pair to a metadata tab.
     *
     * @param tabName the name of the tab to add the key value pair to
     * @param key     the key of the metadata to add
     * @param value   the metadata value to add
     * @return the modified report
     */
    public Report addToTab(String tabName, String key, Object value) {
        diagnostics.metaData.addToTab(tabName, key, value);
        return this;
    }

    /**
     * Clear all of the keys from the given tab.
     *
     * @param tabName the name of the tab to clear.
     * @return The message from the exception contained in this error report.
     */
    public Report clearTab(String tabName) {
        diagnostics.metaData.clearTab(tabName);
        return this;
    }

    /**
     * Add some application info on the report.
     *
     * @param key   the key of app info to add
     * @param value the value of app info to add
     * @return the modified report
     */
    public Report setAppInfo(String key, Object value) {
        diagnostics.app.put(key, value);
        return this;
    }

    /**
     * Set the API key for the report.
     *
     * @param apiKey the API key to use in the report
     * @return the modified report
     */
    public Report setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Get the API key in the report.
     *
     * @return the API key in the report
     */
    protected String getApiKey() {
        return this.apiKey;
    }

    /**
     * Set context of the report.
     *
     * @param context the context to use in the report
     * @return the modified report
     */
    public Report setContext(String context) {
        diagnostics.context = context;
        return this;
    }

    /**
     * Set device information on the report.
     *
     * @param key   the key of device info to add
     * @param value the value of device info to add
     * @return the modified report
     */
    public Report setDeviceInfo(String key, Object value) {
        diagnostics.device.put(key, value);
        return this;
    }

    /**
     * Set the grouping hash on the report. Events will the same grouping hash will be grouped into
     * the same error in Bugsnag. For use if custom grouping is required.
     *
     * @param groupingHash the grouping hash for the error report
     * @return the modified report
     */
    public Report setGroupingHash(String groupingHash) {
        this.groupingHash = groupingHash;
        return this;
    }

    /**
     * Set the severity to use in the report.
     *
     * @param severity the severity for the error report
     * @return the modified report
     */
    public Report setSeverity(Severity severity) {
        this.severity = severity;
        return this;
    }

    /**
     * Helper method to set all the user attributes.
     *
     * @param id    the identifier of the user.
     * @param email the email of the user.
     * @param name  the name of the user.
     * @return the modified report.
     */
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

    static class SeverityReason {
        private final String type;

        SeverityReason(String type) {
            this.type = type;
        }

        @Expose
        String getType() {
            return type;
        }
    }
}
