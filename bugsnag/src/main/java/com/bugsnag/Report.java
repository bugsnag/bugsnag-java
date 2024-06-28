package com.bugsnag;

import com.bugsnag.serialization.Expose;

import com.bugsnag.util.RedactedMap;
import com.bugsnag.util.FilteredMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {

    static final String PAYLOAD_VERSION = "4";

    private Configuration config;

    private String apiKey;
    private final Exception exception;
    private HandledState handledState;
    private Severity severity;
    private String groupingHash;
    private Diagnostics diagnostics;
    private boolean shouldCancel = false;
    private Map<String, Object> sessionMap;
    private final List<ThreadState> threadStates;

    /**
     * Create a report for the error.
     *
     * @param config    the configuration for the report.
     * @param throwable the error to create the report for.
     */
    protected Report(Configuration config, Throwable throwable) {
        this(config, throwable, HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION), Thread.currentThread());
    }

    Report(Configuration config, Throwable throwable,
           HandledState handledState, Thread currentThread) {
        this.config = config;
        this.exception = new Exception(config, throwable);
        this.handledState = handledState;
        this.severity = handledState.getOriginalSeverity();
        diagnostics = new Diagnostics(this.config);

        if (config.sendThreads) {
            Throwable exc = handledState.isUnhandled() ? throwable : null;
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            threadStates = ThreadState.getLiveThreads(config, currentThread, allStackTraces, exc);
        } else {
            threadStates = null;
        }
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
        exceptions.add(exception);

        Throwable currentThrowable = exception.getThrowable().getCause();
        while (currentThrowable != null) {
            exceptions.add(new Exception(config, currentThrowable));
            currentThrowable = currentThrowable.getCause();
        }

        return exceptions;
    }

    @Expose
    boolean getUnhandled() {
        return handledState.isUnhandled();
    }

    @Expose
    SeverityReason getSeverityReason() {
        return new SeverityReason(handledState.calculateSeverityReasonType().toString(),
                handledState.getSeverityReasonAttributes());
    }

    @Expose
    protected List<ThreadState> getThreads() {
        return threadStates;
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
    public Map<String, Object> getApp() {
        return diagnostics.app;
    }

    @Expose
    public Map<String, Object> getDevice() {
        return diagnostics.device;
    }

    @Expose
    public Map<String, String> getUser() {
        return diagnostics.user;
    }

    @Expose
    public Map<String, Object> getFilteredMetaData() {
        return new FilteredMap(diagnostics.metaData, Arrays.asList(config.filters));
    }

    @Expose
    public Map<String, Object> getRedactedMetaData() {
        return new RedactedMap(diagnostics.metaData, Arrays.asList(config.redactedKeys));
    }

    @Expose
    Map<String, Object> getSession() {
        return sessionMap;
    }

    void setSession(Session session) {
        if (session == null) {
            sessionMap = null;
        } else {
            sessionMap = new HashMap<String, Object>();
            sessionMap.put("id", session.getId());
            sessionMap.put("startedAt", session.getStartedAt());

            Map<String, Object> handledCounts = new HashMap<String, Object>();
            handledCounts.put("handled", session.getHandledCount());
            handledCounts.put("unhandled", session.getUnhandledCount());
            sessionMap.put("events", handledCounts);
        }
    }

    /**
     * @return The {@linkplain Throwable exception} which triggered this error report.
     */
    public Throwable getException() {
        return exception.getThrowable();
    }

    /**
     * @return the class name from the exception contained in this error report.
     */
    public String getExceptionName() {
        return exception.getErrorClass();
    }

    /**
     * Sets the class name from the exception contained in this error report.
     *
     * @param exceptionName the error name
     */
    public void setExceptionName(String exceptionName) {
        exception.setErrorClass(exceptionName);
    }

    /**
     * @return The message from the exception contained in this error report.
     */
    public String getExceptionMessage() {
        return exception.getThrowable().getLocalizedMessage();
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
     * @deprecated use {@link #addToTab(String, String, Object)} instead
     */
    @Deprecated
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
     * @deprecated use {@link #addToTab(String, String, Object)} instead
     */
    @Deprecated
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
        this.handledState.setCurrentSeverity(severity);
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

    HandledState getHandledState() {
        return handledState;
    }

    void setHandledState(HandledState handledState) {
        this.handledState = handledState;
    }

    void mergeMetaData(MetaData metaData) {
        diagnostics.metaData.merge(metaData);
    }

    static class SeverityReason {
        private final String type;
        private final Map<String, String> attributes;

        SeverityReason(String type, Map<String, String> attributes) {
            this.type = type;
            this.attributes = attributes;
        }

        @Expose
        String getType() {
            return type;
        }

        @Expose
        Map<String, String> getAttributes() {
            return attributes;
        }
    }
}
