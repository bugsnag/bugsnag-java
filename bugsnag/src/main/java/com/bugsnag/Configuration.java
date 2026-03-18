package com.bugsnag;

import com.bugsnag.callbacks.AppCallback;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.callbacks.JakartaServletCallback;
import com.bugsnag.delivery.AsyncHttpDelivery;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.serialization.DefaultSerializer;
import com.bugsnag.serialization.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@SuppressWarnings("visibilitymodifier")
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bugsnag.class);
    private static final String HEADER_API_PAYLOAD_VERSION = "Bugsnag-Payload-Version";
    private static final String HEADER_API_KEY = "Bugsnag-Api-Key";
    private static final String HEADER_BUGSNAG_SENT_AT = "Bugsnag-Sent-At";

    private String apiKey;
    private String appType;
    private String appVersion;
    private Delivery delivery;
    private EndpointConfiguration endpoints;
    private Delivery sessionDelivery;
    private String[] redactedKeys = new String[] {"password", "secret", "Authorization", "Cookie"};
    private Set<Pattern> discardClassRegexPatterns = new HashSet<Pattern>();
    private Set<String> discardClassStringPatterns = new HashSet<String>();
    private Set<String> enabledReleaseStages = null;
    private String[] projectPackages;
    private String releaseStage;
    private boolean sendThreads = false;
    private Serializer serializer = new DefaultSerializer();

    Collection<Callback> callbacks = new ConcurrentLinkedQueue<Callback>();
    private final AtomicBoolean autoCaptureSessions = new AtomicBoolean(true);
    private final AtomicBoolean sendUncaughtExceptions = new AtomicBoolean(true);
    private final FeatureFlagStore featureFlagStore = new FeatureFlagStore();

    Configuration(String apiKey) {
        this.apiKey = apiKey;
        // Add built-in callbacks
        addCallback(new AppCallback(this));
        addCallback(new DeviceCallback());
        DeviceCallback.initializeCache();

        endpoints = EndpointConfiguration.fromApiKey(apiKey);

        this.delivery = new AsyncHttpDelivery(endpoints.getNotifyEndpoint());
        this.sessionDelivery = new AsyncHttpDelivery(endpoints.getSessionEndpoint());

        if (JakartaServletCallback.isAvailable()) {
            addCallback(new JakartaServletCallback());
        }
    }

    boolean shouldNotifyForReleaseStage() {
        if (enabledReleaseStages == null) {
            return true;
        }
        return enabledReleaseStages.contains(releaseStage);
    }

    boolean shouldIgnoreClass(String className) {
        if (discardClassRegexPatterns == null || discardClassRegexPatterns.isEmpty()) {
            return false;
        }

        for (Pattern pattern : discardClassRegexPatterns) {
            if (pattern.matcher(className).matches()) {
                return true;
            }
        }
        return false;
    }

    void addCallback(Callback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    boolean inProject(String className) {
        if (projectPackages != null) {
            for (String packageName : projectPackages) {
                if (packageName != null && className.startsWith(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setAutoCaptureSessions(boolean autoCaptureSessions) {
        this.autoCaptureSessions.set(autoCaptureSessions);
    }

    public boolean shouldAutoCaptureSessions() {
        return autoCaptureSessions.get();
    }

    public void setSendUncaughtExceptions(boolean sendUncaughtExceptions) {
        this.sendUncaughtExceptions.set(sendUncaughtExceptions);
    }

    public boolean shouldSendUncaughtExceptions() {
        return sendUncaughtExceptions.get();
    }

    /**
     * @deprecated Use {@link #setEndpoints(EndpointConfiguration)} instead.
     */
    @Deprecated
    public void setEndpoints(String notify, String sessions) throws IllegalArgumentException {
        setEndpoints(new EndpointConfiguration(notify, sessions));
    }

    /**
     * Set the endpoints to send data to. Use this to override the default endpoints
     * if you are using Bugsnag Enterprise to point to your own Bugsnag endpoint.
     * <p>
     * Please note that it is recommended that you set both endpoints. If the notify
     * endpoint is
     * missing, an exception will be thrown. If the session endpoint is missing, a
     * warning will be
     * logged and sessions will not be sent automatically.
     * <p>
     * Note that if you are setting a custom {@link Delivery}, this method should be
     * called after
     * the custom implementation has been set.
     *
     * @param endpointConfiguration the endpoint configuration
     * @throws IllegalArgumentException if the endpoint configuration is null or if
     *                                  the notify endpoint is empty or null
     */
    public void setEndpoints(EndpointConfiguration endpointConfiguration) throws IllegalArgumentException {
        if (endpointConfiguration == null) {
            throw new IllegalArgumentException("Endpoint configuration cannot be null.");
        }
        String notify = endpointConfiguration.getNotifyEndpoint();
        String sessions = endpointConfiguration.getSessionEndpoint();
        if (notify == null || notify.isEmpty()) {
            throw new IllegalArgumentException("Notify endpoint cannot be empty or null.");
        } else {
            if (delivery instanceof HttpDelivery) {
                ((HttpDelivery) delivery).setEndpoint(notify);
            } else {
                LOGGER.warn("Delivery is not instance of HttpDelivery, cannot set notify endpoint");
            }
        }

        boolean invalidSessionsEndpoint = sessions == null || sessions.isEmpty();
        String sessionEndpoint = null;

        if (invalidSessionsEndpoint && this.autoCaptureSessions.get()) {
            LOGGER.warn("The session tracking endpoint has not been"
                    + " set. Session tracking is disabled");
            this.autoCaptureSessions.set(false);
        } else {
            sessionEndpoint = sessions;
        }

        if (sessionDelivery instanceof HttpDelivery) {
            // sessionEndpoint may be invalid (e.g. typo in the protocol) here, which
            // should result in the default
            // HttpDelivery throwing a MalformedUrlException which prevents delivery.
            ((HttpDelivery) sessionDelivery).setEndpoint(sessionEndpoint);
        } else {
            LOGGER.warn("Delivery is not instance of HttpDelivery, cannot set sessions endpoint");
        }
    }

    Map<String, String> getErrorApiHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HEADER_API_PAYLOAD_VERSION, Report.PAYLOAD_VERSION);
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toIso8601(new Date()));
        return map;
    }

    Map<String, String> getSessionApiHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HEADER_API_PAYLOAD_VERSION, "1.0");
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toIso8601(new Date()));
        return map;
    }

    // Accessors
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

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public EndpointConfiguration getEndpointsConfiguration() {
        return endpoints;
    }

    public void setEndpointsConfiguration(EndpointConfiguration endpoints) {
        this.endpoints = endpoints;
    }

    public Delivery getSessionDelivery() {
        return sessionDelivery;
    }

    public void setSessionDelivery(Delivery sessionDelivery) {
        this.sessionDelivery = sessionDelivery;
    }

    public String[] getRedactedKeys() {
        return redactedKeys;
    }

    public void setRedactedKeys(String[] redactedKeys) {
        this.redactedKeys = redactedKeys;
    }

    public Pattern[] getDiscardClasses() {
        return discardClassRegexPatterns.toArray(new Pattern[0]);
    }

    /**
     * Set which exception classes should be ignored (not sent) by Bugsnag.
     * Uses Java regex patterns for matching exception class names.
     *
     * @param discardClasses a list of compiled regex patterns to match exception class names
     */
    public void setDiscardClasses(Pattern[] discardClasses) {
        this.discardClassRegexPatterns.clear();
        this.discardClassStringPatterns.clear();
        if (discardClasses != null) {
            for (Pattern pattern : discardClasses) {
                if (pattern != null) {
                    // Store pattern
                    this.discardClassRegexPatterns.add(pattern);
                    // Store string representation for serialization
                    this.discardClassStringPatterns.add(pattern.pattern());
                }
            }
        }
    }

    /**
     * Set which exception classes should be ignored (not sent) by Bugsnag.
     * Compiles the provided strings as Java regex patterns.
     *
     * @param discardClasses a list of regex pattern strings to match exception class names
     */
    public void setDiscardClassesFromStrings(String[] discardClasses) {
        this.discardClassRegexPatterns.clear();
        this.discardClassStringPatterns.clear();
        if (discardClasses != null) {
            for (String patternStr : discardClasses) {
                if (patternStr != null && !patternStr.isEmpty()) {
                    // Store original pattern string
                    this.discardClassStringPatterns.add(patternStr);
                    // Compile as regex pattern
                    this.discardClassRegexPatterns.add(Pattern.compile(patternStr));
                }
            }
        }
    }

    public Set<String> getEnabledReleaseStages() {
        return enabledReleaseStages;
    }

    public void setEnabledReleaseStages(Set<String> enabledReleaseStages) {
        this.enabledReleaseStages = enabledReleaseStages;
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

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    /**
     * Add a feature flag with the specified name and variant.
     * If the name already exists, the variant will be updated.
     *
     * @param name the feature flag name
     * @param variant the feature flag variant (can be null)
     */
    public void addFeatureFlag(String name, String variant) {
        featureFlagStore.addFeatureFlag(name, variant);
    }

    /**
     * Add a feature flag with the specified name and no variant.
     *
     * @param name the feature flag name
     */
    public void addFeatureFlag(String name) {
        addFeatureFlag(name, null);
    }

    /**
     * Add multiple feature flags.
     * If any names already exist, their variants will be updated.
     *
     * @param featureFlags the feature flags to add
     */
    public void addFeatureFlags(Collection<FeatureFlag> featureFlags) {
        featureFlagStore.addFeatureFlags(featureFlags);
    }

    /**
     * Remove the feature flag with the specified name.
     *
     * @param name the feature flag name to remove
     */
    public void clearFeatureFlag(String name) {
        featureFlagStore.clearFeatureFlag(name);
    }

    /**
     * Remove all feature flags.
     */
    public void clearFeatureFlags() {
        featureFlagStore.clearFeatureFlags();
    }

    /**
     * Get a copy of the feature flag store.
     *
     * @return a copy of the feature flag store
     */
    FeatureFlagStore copyFeatureFlagStore() {
        return featureFlagStore.copy();
    }
}
