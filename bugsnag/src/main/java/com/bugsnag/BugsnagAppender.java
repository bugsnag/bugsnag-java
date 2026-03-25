package com.bugsnag;

import com.bugsnag.callbacks.OnErrorCallback;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.logback.BugsnagMarker;
import com.bugsnag.logback.LogbackFeatureFlag;
import com.bugsnag.logback.LogbackMetadata;
import com.bugsnag.logback.LogbackMetadataKey;
import com.bugsnag.logback.LogbackMetadataTab;
import com.bugsnag.logback.ProxyConfiguration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.slf4j.Marker;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


/** Sends events to Bugsnag using its Java client library. */
public class BugsnagAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    /** Classes that we should not send logs for (to prevent infinite loops on error) */
    private static final List<String> EXCLUDED_CLASSES = Arrays.asList(
            "com.bugsnag.Bugsnag",
            "com.bugsnag.delivery.OutputStreamDelivery",
            "com.bugsnag.delivery.SyncHttpDelivery");

    /** Logger name patterns that should not cause reports to be sent to Bugsnag. **/
    private static final List<Pattern> EXCLUDED_LOGGER_PATTERNS = new ArrayList<Pattern>();

    /** Bugsnag API key; the appender doesn't do anything if it's not available. */
    private String apiKey;

    /** Application type. */
    private String appType;

    /** Bugsnag error server endpoint. */
    private String endpoint;

    /** Property names that should be redacted before sending to Bugsnag servers. */
    private Set<String> redactedKeys = new HashSet<String>();

    /** Exception classes to be ignored. */
    private Set<String> discardClasses = new HashSet<String>();

    /** Release stages that should be notified. */
    private Set<String> enabledReleaseStages = new HashSet<String>();

    /** Project packages. */
    private Set<String> projectPackages = new HashSet<String>();

    /** Proxy configuration to access the internet. */
    private ProxyConfiguration proxy;

    /** Release stage. */
    private String releaseStage;

    /** Whether thread state should be sent to Bugsnag. */
    private ThreadSendPolicy sendThreads = ThreadSendPolicy.NEVER;

    /** Bugsnag API request timeout. */
    private int timeout;

    /** Application version. */
    private String appVersion;
    private List<LogbackMetadata> globalMetadata = new ArrayList<LogbackMetadata>();

    /** Feature flags configured via logback.xml. */
    private List<LogbackFeatureFlag> featureFlags = new ArrayList<LogbackFeatureFlag>();

    /** Bugsnag client. */
    private Bugsnag bugsnag = null;

    /** Creates an appender from an existing Bugsnag client. */
    public BugsnagAppender(Bugsnag bugsnag) {
        super();
        this.bugsnag = bugsnag;
    }

    /**
     * Default constructor used by Logback to create a Bugsnag appender that will create it's own
     * client when required.
     */
    @SuppressWarnings("unused")
    public BugsnagAppender() {
        this(null);
    }

    @Override
    public void start() {
        if (bugsnag == null) {
            this.bugsnag = createBugsnag();
        }
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (bugsnag != null) {
            bugsnag.close();
        }
    }

    @Override
    protected void append(final ILoggingEvent loggingEvent) {
        if (bugsnag != null) {
            Throwable throwable = extractThrowable(loggingEvent);

            final OnErrorCallback reportCallback;
            Marker marker = loggingEvent.getMarker();
            if (marker instanceof BugsnagMarker) {
                reportCallback = ((BugsnagMarker) marker).getCallback();
            } else {
                reportCallback = null;
            }

            // Only send a message if there is an exception, the log does not come
            // from the this library and the logger is not in the list of excluded loggers.
            if (throwable != null
                    && !detectLogFromBugsnag(throwable)
                    && !isExcludedLogger(loggingEvent.getLoggerName())) {
                bugsnag.notify(
                        throwable,
                        calculateSeverity(loggingEvent),
                        new OnErrorCallback() {
                            @Override
                            public boolean onError(BugsnagEvent event) {

                                // Add some data from the logging event
                                event.addMetadata("Log event data",
                                        "Message", loggingEvent.getFormattedMessage());
                                event.addMetadata("Log event data",
                                        "Logger name", loggingEvent.getLoggerName());

                                // Add details from the logging context to the event
                                populateContextData(event, loggingEvent);

                                if (reportCallback != null) {
                                    boolean proceed = reportCallback.onError(event);
                                    if (!proceed) {
                                        return false; // suppress delivery
                                    }
                                }
                                return true;
                            }
                        });
            }
        }
    }

    /**
     * Adds logging context values to the given report meta data
     *
     * @param report The report being sent to Bugsnag
     * @param event The logging event
     */
    private void populateContextData(BugsnagEvent report, ILoggingEvent event) {
        Map<String, String> propertyMap = event.getMDCPropertyMap();

        if (propertyMap != null) {
            // Loop through all the keys and put them in the correct tabs

            for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                report.addMetadata("Context", entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Calculates the severity based on the logging event
     * @param event the event
     * @return The Bugsnag severity
     */
    private Severity calculateSeverity(ILoggingEvent event) {
        if (event.getLevel().equals(Level.ERROR)) {
            return Severity.ERROR;
        } else if (event.getLevel().equals(Level.WARN)) {
            return Severity.WARNING;
        }
        return Severity.INFO;
    }

    /**
     * Checks to see if a stack trace came from the Bugsnag library
     * (prevent possible infinite reporting loops)
     *
     * @param throwable the exception to check
     * @return true if the stacktrace contains a frame from the Bugsnag library
     */
    private boolean detectLogFromBugsnag(Throwable throwable) {
        // Check all places that LOGGER is called with an exception in the Bugsnag library
        for (StackTraceElement element : throwable.getStackTrace()) {
            for (String excludedClass : EXCLUDED_CLASSES) {
                if (element.getClassName().startsWith(excludedClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the throwable from the log event (if any)
     * @param event The log event
     * @return The throwable (or null)
     */
    private Throwable extractThrowable(ILoggingEvent event) {
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy instanceof ThrowableProxy) {
            return ((ThrowableProxy) throwableProxy).getThrowable();
        }

        return null;
    }

    /**
     * @return Create a Bugsnag instance with parameters from the logback configuration
     */
    private Bugsnag createBugsnag() {
        Bugsnag bugsnag = new Bugsnag(apiKey, false);
        bugsnag.setAutoCaptureSessions(false);

        if (appType != null) {
            bugsnag.setAppType(appType);
        }

        if (appVersion != null) {
            bugsnag.setAppVersion(appVersion);
        }

        if (endpoint != null) {
            bugsnag.setEndpoints(new EndpointConfiguration(endpoint, ""));
        }

        if (proxy != null) {
            bugsnag.setProxy(
                    new Proxy(
                            proxy.getType(),
                            new InetSocketAddress(proxy.getHostname(), proxy.getPort())));
        }

        if (releaseStage != null) {
            bugsnag.setReleaseStage(releaseStage);
        }

        if (timeout > 0) {
            bugsnag.setTimeout(timeout);
        }

        if (!redactedKeys.isEmpty()) {
            bugsnag.setRedactedKeys(redactedKeys.toArray(new String[0]));
        }

        bugsnag.setDiscardClasses(compileDiscardPatterns(discardClasses));

        if (!enabledReleaseStages.isEmpty()) {
            bugsnag.setEnabledReleaseStages(enabledReleaseStages.toArray(new String[0]));
        }

        bugsnag.setProjectPackages(projectPackages.toArray(new String[0]));
        bugsnag.setSendThreads(sendThreads);

        // Add feature flags
        for (LogbackFeatureFlag flag : featureFlags) {
            bugsnag.addFeatureFlag(flag.getName(), flag.getVariant());
        }

        // Add a callback to put global metadata on every report
        bugsnag.addOnError(new OnErrorCallback() {
            @Override
            public boolean onError(BugsnagEvent event) {

                for (LogbackMetadata metadata : globalMetadata) {
                    for (LogbackMetadataTab tab : metadata.getTabs()) {
                        for (LogbackMetadataKey key : tab.getKeys()) {
                            event.addMetadata(tab.getName(),
                                    key.getName(),
                                    key.getValue());
                        }
                    }

                }
                return true;
            }
        });

        return bugsnag;
    }

    /**
     * Compiles a collection of pattern strings into an array of Pattern objects.
     *
     * @param patternStrings the collection of pattern strings to compile
     * @return an array of compiled Pattern objects
     */
    private Pattern[] compileDiscardPatterns(Collection<String> patternStrings) {
        Pattern[] patterns = new Pattern[patternStrings.size()];
        int idx = 0;
        for (String pattern : patternStrings) {
            patterns[idx++] = Pattern.compile(pattern);
        }
        return patterns;
    }

    /**
     * Add a callback to execute code before/after every notification to Bugsnag.
     *
     * <p>You can use this to add or modify information attached to an error
     * before it is sent to your dashboard. You can also stop any reports being
     * sent to Bugsnag completely.
     *
     * @param callback a callback to run before sending errors to Bugsnag
     * @see OnErrorCallback
     */
    public void addCallback(OnErrorCallback callback) {
        if (bugsnag != null) {
            bugsnag.addOnError(callback);
        }
    }

    /**
     * Set the method of delivery for Bugsnag error report. By default we'll
     * send reports asynchronously using a thread pool to
     * https://notify.bugsnag.com, but you can override this to use a
     * different sending technique or endpoint (for example, if you are using
     * Bugsnag On-Premise).
     *
     * @param delivery the delivery mechanism to use
     * @see Delivery
     */
    public void setDelivery(Delivery delivery) {
        if (bugsnag != null) {
            bugsnag.setDelivery(delivery);
        }
    }

    /**
     * Set the method of delivery for Bugsnag sessions. By default we'll
     * send sessions asynchronously using a thread pool to
     * https://sessions.bugsnag.com, but you can override this to use a
     * different sending technique or endpoint (for example, if you are using
     * Bugsnag On-Premise).
     *
     * @param delivery the delivery mechanism to use
     * @see Delivery
     */
    public void setSessionDelivery(Delivery delivery) {
        if (bugsnag != null) {
            bugsnag.setSessionDelivery(delivery);
        }
    }

    // Setters

    /**
     * Internal use only
     * Should only be used via the logback.xml file
     *
     * @param apiKey The API key to use
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @see Bugsnag#setAppType(String)
     */
    public void setAppType(String appType) {
        this.appType = appType;

        if (bugsnag != null) {
            bugsnag.setAppType(appType);
        }
    }

    /**
     * Internal use only
     * Should only be used via the logback.xml file
     *
     * @see Bugsnag#setEndpoints(String, String)
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;

        if (bugsnag != null) {
            bugsnag.setEndpoints(new EndpointConfiguration(endpoint, ""));
        }
    }

    /**
     * @see Bugsnag#setRedactedKeys(String...)
     */
    public void setRedactedKey(String key) {
        this.redactedKeys.add(key);

        if (bugsnag != null) {
            bugsnag.setRedactedKeys(this.redactedKeys.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setRedactedKeys(String...)
     */
    public void setRedactedKeys(String key) {
        this.redactedKeys.addAll(split(key));

        if (bugsnag != null) {
            bugsnag.setRedactedKeys(this.redactedKeys.toArray(new String[0]));
        }
    }

    @Deprecated
    public void setIgnoredClass(String ignoredClass) {
        setDiscardClass(ignoredClass);
    }

    /**
     * @see Bugsnag#setDiscardClasses(Pattern...)
     */
    public void setDiscardClass(String discardClass) {
        this.discardClasses.add(discardClass);

        if (bugsnag != null) {
            bugsnag.setDiscardClasses(compileDiscardPatterns(this.discardClasses));
        }
    }

    /**
     * @see Bugsnag#setDiscardClasses(Pattern...)
     */
    public void setDiscardClasses(String discardClasses) {
        this.discardClasses.addAll(split(discardClasses));

        if (bugsnag != null) {
            bugsnag.setDiscardClasses(compileDiscardPatterns(this.discardClasses));
        }
    }

    @Deprecated
    public void setNotifyReleaseStage(String notifyReleaseStage) {
        setEnabledReleaseStage(notifyReleaseStage);
    }

    /**
     * @see Bugsnag#setEnabledReleaseStages(String...)
     */
    public void setEnabledReleaseStage(String enabledReleaseStage) {
        this.enabledReleaseStages.add(enabledReleaseStage);

        if (bugsnag != null) {
            bugsnag.setEnabledReleaseStages(this.enabledReleaseStages.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setEnabledReleaseStages(String...)
     */
    public void setEnabledReleaseStages(String enabledReleaseStages) {
        this.enabledReleaseStages.addAll(split(enabledReleaseStages));

        if (bugsnag != null) {
            bugsnag.setEnabledReleaseStages(this.enabledReleaseStages.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setProjectPackages(String...)
     */
    public void setProjectPackage(String projectPackage) {
        this.projectPackages.add(projectPackage);

        if (bugsnag != null) {
            bugsnag.setProjectPackages(this.projectPackages.toArray(new String[0]));
        }
    }


    /**
     * @see Bugsnag#setProjectPackages(String...)
     */
    public void setProjectPackages(String projectPackages) {
        this.projectPackages.addAll(split(projectPackages));

        if (bugsnag != null) {
            bugsnag.setProjectPackages(this.projectPackages.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setProxy(Proxy)
     */
    public void setProxy(ProxyConfiguration proxy) {
        this.proxy = proxy;

        if (bugsnag != null) {
            bugsnag.setProxy(
                    new Proxy(
                            proxy.getType(),
                            new InetSocketAddress(proxy.getHostname(), proxy.getPort())));
        }
    }

    /**
     * @see Bugsnag#setReleaseStage(String)
     */
    public void setReleaseStage(String releaseStage) {
        this.releaseStage = releaseStage;

        if (bugsnag != null) {
            bugsnag.setReleaseStage(releaseStage);
        }
    }

    /**
     * @see Bugsnag#setSendThreads(ThreadSendPolicy)
     */
    public void setSendThreads(ThreadSendPolicy sendThreads) {
        this.sendThreads = sendThreads;

        if (bugsnag != null) {
            bugsnag.setSendThreads(sendThreads);
        }
    }

    /**
     * @see Bugsnag#setTimeout(int)
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;

        if (bugsnag != null) {
            bugsnag.setTimeout(timeout);
        }
    }

    /**
     * @see Bugsnag#setAppVersion(String)
     */
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;

        if (bugsnag != null) {
            bugsnag.setAppVersion(appVersion);
        }
    }

    /**
     * Internal use only
     * Should only be used via the logback.xml file
     *
     * @param metadata Adds metadata to every report
     */
    public void setMetadata(LogbackMetadata metadata) {
        this.globalMetadata.add(metadata);
    }

    @Deprecated
    public void setMetaData(LogbackMetadata metadata) {
        setMetadata(metadata);
    }

    /**
     * Add a regex logger name pattern to match loggers that should not generate Bugsnag reports
     *
     * @param loggerNameRegex The regex pattern for logger names that should be excluded
     */
    static void addExcludedLoggerPattern(String loggerNameRegex) {
        EXCLUDED_LOGGER_PATTERNS.add(Pattern.compile(loggerNameRegex));
    }

    /**
     * Splits the given string on commas
     * @param value The string to split
     * @return The list of parts
     */
    List<String> split(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        String[] parts = value.split(",", -1);
        return Arrays.asList(parts);
    }

    /**
     * @return The Bugsnag instance
     */
    public Bugsnag getClient() {
        return bugsnag;
    }

    /**
     * Whether or not a logger is excluded from generating Bugsnag reports
     * @param loggerName The name of the logger
     * @return true if the logger should be excluded
     */
    private boolean isExcludedLogger(String loggerName) {
        for (Pattern excludedLoggerPattern : EXCLUDED_LOGGER_PATTERNS) {
            if (excludedLoggerPattern.matcher(loggerName).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a feature flag with a name and variant.
     * This is typically configured via logback.xml.
     *
     * @param name the feature flag name
     * @param variant the feature flag variant (can be null)
     */
    public void addFeatureFlag(String name, String variant) {
        LogbackFeatureFlag flag = new LogbackFeatureFlag();
        flag.setName(name);
        flag.setVariant(variant);
        featureFlags.add(flag);

        if (bugsnag != null) {
            bugsnag.addFeatureFlag(name, variant);
        }
    }

    /**
     * Add a feature flag with just a name (no variant).
     * This is typically configured via logback.xml.
     *
     * @param name the feature flag name
     */
    public void addFeatureFlag(String name) {
        addFeatureFlag(name, null);
    }

    /**
     * Add a feature flag from logback.xml configuration.
     * Internal use only - should only be used via the logback.xml file.
     *
     * @param flag the feature flag to add
     */
    public void setFeatureFlag(LogbackFeatureFlag flag) {
        featureFlags.add(flag);

        if (bugsnag != null) {
            bugsnag.addFeatureFlag(flag.getName(), flag.getVariant());
        }
    }

    /**
     * Clear a feature flag by name.
     *
     * @param name the feature flag name to remove
     */
    public void clearFeatureFlag(String name) {
        featureFlags.removeIf(flag -> flag.getName() != null && flag.getName().equals(name));

        if (bugsnag != null) {
            bugsnag.clearFeatureFlag(name);
        }
    }

    /**
     * Clear all feature flags.
     */
    public void clearFeatureFlags() {
        featureFlags.clear();

        if (bugsnag != null) {
            bugsnag.clearFeatureFlags();
        }
    }
}
