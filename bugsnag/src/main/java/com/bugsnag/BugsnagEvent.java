package com.bugsnag;

import com.bugsnag.serialization.Expose;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BugsnagEvent {

    static final String PAYLOAD_VERSION = "4";

    private Configuration config;

    private String apiKey;
    private final BugsnagError error;
    private HandledState handledState;
    private Severity severity;
    private String groupingHash;
    private Diagnostics diagnostics;
    private Map<String, Object> sessionMap;
    private final List<BugsnagThread> threads;
    private final FeatureFlagStore featureFlagStore;

    /**
     * Create a report for the error.
     *
     * @param config    the configuration for the report.
     * @param throwable the error to create the report for.
     */
    protected BugsnagEvent(Configuration config, Throwable throwable) {
        this(config, throwable, HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION), Thread.currentThread(), null);
    }

    BugsnagEvent(Configuration config, Throwable throwable,
                 HandledState handledState, Thread currentThread) {
        this(config, throwable, handledState, currentThread, null);
    }

    BugsnagEvent(Configuration config, Throwable throwable,
                 HandledState handledState, Thread currentThread, FeatureFlagStore clientFeatureFlagStore) {
        this.config = config;
        this.error = new BugsnagError(config, throwable);
        this.handledState = handledState;
        this.severity = handledState.getOriginalSeverity();
        diagnostics = new Diagnostics(this.config);

        // Initialize feature flags: start with config, then merge client flags
        featureFlagStore = config.copyFeatureFlagStore();
        if (clientFeatureFlagStore != null) {
            featureFlagStore.merge(clientFeatureFlagStore);
        }

        boolean sendThreads = config.getSendThreads() == ThreadSendPolicy.ALWAYS ||
                (config.getSendThreads() == ThreadSendPolicy.UNHANDLED_ONLY && handledState.isUnhandled());
        if (sendThreads) {
            Throwable exc = handledState.isUnhandled() ? throwable : null;
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            threads = BugsnagThread.getLiveThreads(config, currentThread, allStackTraces, exc);
        } else {
            threads = null;
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
    protected List<BugsnagError> getErrors() {
        List<BugsnagError> errors = new ArrayList<BugsnagError>();
        errors.add(error);

        Throwable currentThrowable = error.getThrowable().getCause();
        while (currentThrowable != null) {
            errors.add(new BugsnagError(config, currentThrowable));
            currentThrowable = currentThrowable.getCause();
        }

        return errors;
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
    protected List<BugsnagThread> getThreads() {
        return threads;
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
    @JsonProperty("metaData")
    public Map<String, Object> getMetadata() {
        return new RedactedMap(diagnostics.metadata, Set.of(config.getRedactedKeys()));
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
     * @return The {@linkplain Throwable exception} which triggered this error
     *         report.
     */
    public Throwable getException() {
        return error.getThrowable();
    }

    /**
     * @return the class name from the exception contained in this error report.
     */
    public String getExceptionName() {
        return error.getErrorClass();
    }

    /**
     * Sets the class name from the exception contained in this error report.
     *
     * @param exceptionName the error name
     */
    public void setExceptionName(String exceptionName) {
        error.setErrorClass(exceptionName);
    }

    /**
     * @return The message from the exception contained in this error report.
     */
    public String getExceptionMessage() {
        return error.getThrowable().getLocalizedMessage();
    }

    /**
     * Add a key value pair to a metadata tab.
     *
     * @param tabName the name of the tab to add the key value pair to
     * @param key     the key of the metadata to add
     * @param value   the metadata value to add
     * @return the modified report
     */
    public BugsnagEvent addMetadata(String tabName, String key, Object value) {
        diagnostics.metadata.addMetadata(tabName, key, value);
        return this;
    }

    /**
     * Clear all of the keys from the given tab.
     *
     * @param tabName the name of the tab to clear.
     * @return The message from the exception contained in this error report.
     */
    public BugsnagEvent clearTab(String tabName) {
        diagnostics.metadata.clearMetadata(tabName);
        return this;
    }

    /**
     * Add some application info on the report.
     *
     * @param key   the key of app info to add
     * @param value the value of app info to add
     * @return the modified report
     * @deprecated use {@link #addMetadata(String, String, Object)} instead
     */
    @Deprecated
    public BugsnagEvent setAppInfo(String key, Object value) {
        diagnostics.app.put(key, value);
        return this;
    }

    /**
     * Set the API key for the report.
     *
     * @param apiKey the API key to use in the report
     * @return the modified report
     */
    public BugsnagEvent setApiKey(String apiKey) {
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
    public BugsnagEvent setContext(String context) {
        diagnostics.context = context;
        return this;
    }

    /**
     * Set device information on the report.
     *
     * @param key   the key of device info to add
     * @param value the value of device info to add
     * @return the modified report
     * @deprecated use {@link #addMetadata(String, String, Object)} instead
     */
    @Deprecated
    public BugsnagEvent setDeviceInfo(String key, Object value) {
        diagnostics.device.put(key, value);
        return this;
    }

    /**
     * Set the grouping hash on the report. Events will the same grouping hash will
     * be grouped into
     * the same error in Bugsnag. For use if custom grouping is required.
     *
     * @param groupingHash the grouping hash for the error report
     * @return the modified report
     */
    public BugsnagEvent setGroupingHash(String groupingHash) {
        this.groupingHash = groupingHash;
        return this;
    }

    /**
     * Set the severity to use in the report.
     *
     * @param severity the severity for the error report
     * @return the modified report
     */
    public BugsnagEvent setSeverity(Severity severity) {
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
    public BugsnagEvent setUser(String id, String email, String name) {
        diagnostics.user.put("id", id);
        diagnostics.user.put("email", email);
        diagnostics.user.put("name", name);
        return this;
    }

    public BugsnagEvent setUserId(String id) {
        diagnostics.user.put("id", id);
        return this;
    }

    public BugsnagEvent setUserEmail(String email) {
        diagnostics.user.put("email", email);
        return this;
    }

    public BugsnagEvent setUserName(String name) {
        diagnostics.user.put("name", name);
        return this;
    }

    HandledState getHandledState() {
        return handledState;
    }

    void setHandledState(HandledState handledState) {
        this.handledState = handledState;
    }

    void mergeMetadata(Metadata metadata) {
        diagnostics.metadata.merge(metadata);
    }

    /**
     * Get the list of feature flags for this report.
     * The order reflects when flags were first added across Configuration, Client, and Event scopes.
     *
     * @return an unmodifiable list of feature flags
     */
    @Expose
    public List<FeatureFlag> getFeatureFlags() {
        return featureFlagStore.toList();
    }

    /**
     * Add a feature flag with the specified name and variant.
     * If the name already exists, the variant will be updated.
     *
     * @param name the feature flag name
     * @param variant the feature flag variant (can be null)
     * @return the modified report
     */
    public BugsnagEvent addFeatureFlag(String name, String variant) {
        featureFlagStore.addFeatureFlag(name, variant);
        return this;
    }

    /**
     * Add a feature flag with the specified name and no variant.
     *
     * @param name the feature flag name
     * @return the modified report
     */
    public BugsnagEvent addFeatureFlag(String name) {
        return addFeatureFlag(name, null);
    }

    /**
     * Add multiple feature flags.
     * If any names already exist, their variants will be updated.
     *
     * @param featureFlags the feature flags to add
     * @return the modified report
     */
    public BugsnagEvent addFeatureFlags(Collection<FeatureFlag> featureFlags) {
        featureFlagStore.addFeatureFlags(featureFlags);
        return this;
    }

    /**
     * Remove the feature flag with the specified name.
     *
     * @param name the feature flag name to remove
     * @return the modified report
     */
    public BugsnagEvent clearFeatureFlag(String name) {
        featureFlagStore.clearFeatureFlag(name);
        return this;
    }

    /**
     * Remove all feature flags.
     *
     * @return the modified report
     */
    public BugsnagEvent clearFeatureFlags() {
        featureFlagStore.clearFeatureFlags();
        return this;
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
