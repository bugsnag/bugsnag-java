package com.bugsnag;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.logback.BugsnagMarker;
import com.bugsnag.logback.LogbackEndpoints;
import com.bugsnag.logback.LogbackMetaData;
import com.bugsnag.logback.LogbackMetaDataKey;
import com.bugsnag.logback.LogbackMetaDataTab;
import com.bugsnag.logback.ProxyConfiguration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    /** Whether or not to send unhandled exceptions to Bugsnag */
    private boolean sendUncaughtExceptions = true;

    /** Whether or not to automatically capture session information */
    private boolean autoCaptureSessions = true;

    /** Application type. */
    private String appType;

    /** Bugsnag error/session server endpoints. */
    private LogbackEndpoints logbackEndpoints;

    /** Property names that should be filtered out before sending to Bugsnag servers. */
    private Set<String> filteredProperties = new HashSet<String>();

    /** Exception classes to be ignored. */
    private Set<String> ignoredClasses = new HashSet<String>();

    /** Release stages that should be notified. */
    private Set<String> notifyReleaseStages = new HashSet<String>();

    /** Project packages. */
    private Set<String> projectPackages = new HashSet<String>();

    /** Proxy configuration to access the internet. */
    private ProxyConfiguration proxy;

    /** Release stage. */
    private String releaseStage;

    /** Whether thread state should be sent to Bugsnag. */
    private boolean sendThreads = false;

    /** Bugsnag API request timeout. */
    private int timeout;

    /** Application version. */
    private String appVersion;

    private List<LogbackMetaData> globalMetaData = new ArrayList<LogbackMetaData>();

    /** Bugsnag client. */
    private Bugsnag bugsnag = null;

    /** The appender instance */
    private static Map<String, BugsnagAppender> instances = new HashMap<String, BugsnagAppender>();

    /**
     * @return A running instance of the appender (if one has been created)
     */
    public static BugsnagAppender getInstance() {
        if (instances.size() == 0) {
            return null;
        } else  if (instances.size() == 1) {
            return instances.get(instances.keySet().toArray(new String[1])[0]);
        } else {
            throw new IllegalStateException(
                    "Multiple log appenders have been created, please supply API key parameter");
        }
    }

    /**
     * @param apiKey The API key of the appender to get (only required if using multiple API keys)
     * @return A running instance of the appender (if one has been created)
     */
    public static BugsnagAppender getInstance(String apiKey) {
        if (instances.containsKey(apiKey)) {
            return instances.get(apiKey);
        } else {
            return null;
        }
    }

    @Override
    public void start() {
        if (apiKey != null && !apiKey.isEmpty()) {
            this.bugsnag = createBugsnag();

            instances.put(apiKey, this);
        }
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (bugsnag != null) {
            bugsnag.close();
            instances.remove(apiKey);
        }
    }

    @Override
    protected void append(final ILoggingEvent event) {
        if (bugsnag != null) {
            Throwable throwable = extractThrowable(event);

            final Callback reportCallback;
            if (event.getMarker() != null && event.getMarker() instanceof BugsnagMarker) {
                reportCallback = ((BugsnagMarker) event.getMarker()).getCallback();
            } else {
                reportCallback = null;
            }

            // Only send a message if there is an exception, the log does not come
            // from the this library and the logger is not in the list of excluded loggers.
            if (throwable != null
                    && !detectLogFromBugsnag(throwable)
                    && !isExcludedLogger(event.getLoggerName())) {
                bugsnag.notify(
                        throwable,
                        calculateSeverity(event),
                        new Callback() {
                            @Override
                            public void beforeNotify(Report report) {

                                // Add some data from the logging event
                                report.addToTab("Log event data",
                                        "Message", event.getMessage());
                                report.addToTab("Log event data",
                                        "Timestamp", event.getTimeStamp());

                                if (reportCallback != null) {
                                    reportCallback.beforeNotify(report);
                                }
                            }
                        });
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
        Bugsnag bugsnag = Bugsnag.init(apiKey, sendUncaughtExceptions);

        bugsnag.setLogbackAppenderInUse();

        bugsnag.setAutoCaptureSessions(autoCaptureSessions);

        if (appType != null) {
            bugsnag.setAppType(appType);
        }

        if (appVersion != null) {
            bugsnag.setAppVersion(appVersion);
        }

        if (logbackEndpoints != null) {
            bugsnag.setEndpoints(logbackEndpoints.getNotifyEndpoint(),
                    logbackEndpoints.getSessionEndpoint());
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

        if (filteredProperties.size() > 0) {
            bugsnag.setFilters(filteredProperties.toArray(new String[0]));
        }

        bugsnag.setIgnoreClasses(ignoredClasses.toArray(new String[0]));

        if (notifyReleaseStages.size() > 0) {
            bugsnag.setNotifyReleaseStages(notifyReleaseStages.toArray(new String[0]));
        }

        bugsnag.setProjectPackages(projectPackages.toArray(new String[0]));
        bugsnag.setSendThreads(sendThreads);

        // Add a callback to put global meta data on every report
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {

                for (LogbackMetaData metaData : globalMetaData) {
                    for (LogbackMetaDataTab tab : metaData.getTabs()) {
                        for (LogbackMetaDataKey key : tab.getKeys()) {
                            report.addToTab(tab.getName(),
                                    key.getName(),
                                    key.getValue());
                        }
                    }

                }
            }
        });

        return bugsnag;
    }

    /**
     * Add a callback to execute code before/after every notification to Bugsnag.
     *
     * <p>You can use this to add or modify information attached to an error
     * before it is sent to your dashboard. You can also stop any reports being
     * sent to Bugsnag completely.
     *
     * @param callback a callback to run before sending errors to Bugsnag
     * @see Callback
     */
    public void addCallback(Callback callback) {
        if (bugsnag != null) {
            bugsnag.addCallback(callback);
        }
    }

    /**
     * Manually starts tracking a new session.
     *
     * Note: sessions are currently tracked on a per-thread basis. Therefore, if this method were
     * called from Thread A then Thread B, two sessions would be considered 'active'. Any custom
     * strategy used to track sessions should take this into account.
     *
     * Automatic session tracking can be enabled via
     * {@link BugsnagAppender#setAutoCaptureSessions(boolean)}, which will automatically
     * create a new session for each request
     */
    public void startSession() {
        if (bugsnag != null) {
            bugsnag.startSession();
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
     * Internal use only
     * Should only be used via the logback.xml file
     *
     * @param sendUncaughtExceptions Whether or not Bugsnag should catch unhandled exceptions
     */
    public void setSendUncaughtExceptions(boolean sendUncaughtExceptions) {
        this.sendUncaughtExceptions = sendUncaughtExceptions;
    }

    /**
     * @see Bugsnag#setAutoCaptureSessions(boolean)
     */
    public void setAutoCaptureSessions(boolean autoCaptureSessions) {
        this.autoCaptureSessions = autoCaptureSessions;

        if (bugsnag != null) {
            bugsnag.setAutoCaptureSessions(autoCaptureSessions);
        }
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
    public void setEndpoints(LogbackEndpoints logbackEndpoints) {
        this.logbackEndpoints = logbackEndpoints;

        if (bugsnag != null) {
            bugsnag.setEndpoints(logbackEndpoints.getNotifyEndpoint(),
                    logbackEndpoints.getSessionEndpoint());
        }
    }

    /**
     * @see Bugsnag#setFilters(String...)
     */
    public void setFilteredProperty(String filter) {
        this.filteredProperties.add(filter);

        if (bugsnag != null) {
            bugsnag.setFilters(this.filteredProperties.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setFilters(String...)
     */
    public void setFilteredProperties(String filters) {
        this.filteredProperties.addAll(split(filters));

        if (bugsnag != null) {
            bugsnag.setFilters(this.filteredProperties.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setIgnoreClasses(String...)
     */
    public void setIgnoredClass(String ignoredClass) {
        this.ignoredClasses.add(ignoredClass);

        if (bugsnag != null) {
            bugsnag.setIgnoreClasses(this.ignoredClasses.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setIgnoreClasses(String...)
     */
    public void setIgnoredClasses(String ignoredClasses) {
        this.ignoredClasses.addAll(split(ignoredClasses));

        if (bugsnag != null) {
            bugsnag.setIgnoreClasses(this.ignoredClasses.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setNotifyReleaseStages(String...)
     */
    public void setNotifyReleaseStage(String notifyReleaseStage) {
        this.notifyReleaseStages.add(notifyReleaseStage);

        if (bugsnag != null) {
            bugsnag.setNotifyReleaseStages(this.notifyReleaseStages.toArray(new String[0]));
        }
    }

    /**
     * @see Bugsnag#setNotifyReleaseStages(String...)
     */
    public void setNotifyReleaseStages(String notifyReleaseStages) {
        this.notifyReleaseStages.addAll(split(notifyReleaseStages));

        if (bugsnag != null) {
            bugsnag.setNotifyReleaseStages(this.notifyReleaseStages.toArray(new String[0]));
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
     * @see Bugsnag#setSendThreads(boolean)
     */
    public void setSendThreads(boolean sendThreads) {
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
     * @param metaData Adds meta data to every report
     */
    public void setMetaData(LogbackMetaData metaData) {
        this.globalMetaData.add(metaData);
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
     * @return The Bugsnag instance (used internally only)
     */
    Bugsnag getBugsnag() {
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
}