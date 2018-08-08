package com.bugsnag;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.logback.ProxyConfiguration;
import org.slf4j.MDC;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Sends events to Bugsnag using its Java client library. */
public class BugsnagAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final String LOGGING_CONTEXT_THREAD_PREFIX = "com.bugsnag.BugsnagAppender.thread.";
    private static final String LOGGING_CONTEXT_REPORT_PREFIX = "com.bugsnag.BugsnagAppender.report.";
    private static final String LOGGING_CONTEXT_TAB_SEPARATOR = ".reportTab.";

    /** Bugsnag API key; the appender doesn't do anything if it's not available. */
    private String apiKey;

    /** Application type. */
    private String appType;

    /** Bugsnag error server endpoint. */
    private String notifyEndpoint;

    /** Bugsnag session server endpoint. */
    private String sessionEndpoint;

    /** Property names that should be filtered out before sending to Bugsnag servers. */
    private List<String> filteredProperties = new ArrayList<String>();

    /** Exception classes to be ignored. */
    private List<String> ignoredClasses = new ArrayList<String>();

    /** Release stages that should be notified. */
    private List<String> notifyReleaseStages = new ArrayList<String>();

    /** Project packages. */
    private List<String> projectPackages = new ArrayList<String>();

    /** Proxy configuration to access the internet. */
    private ProxyConfiguration proxy;

    /** Release stage. */
    private String releaseStage;

    /** Whether thread state should be sent to Bugsnag. */
    private boolean sendThreads;

    /** Bugsnag API request timeout. */
    private int timeout;

    /** Application version. */
    private String appVersion;

    /** Bugsnag client. */
    private Bugsnag bugsnag = null;

    /** The appender instance */
    private static BugsnagAppender instance;

    /**
     * @return The running instance of the appender (if one has been created)
     */
    public static BugsnagAppender getInstance() {
        return instance;
    }

    @Override
    public void start() {
        if (apiKey != null && !apiKey.isEmpty()) {
            this.bugsnag = createBugsnag();
            instance = this;
        }
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (bugsnag != null) {
            bugsnag.close();
            instance = null;
        }
    }

    @Override
    protected void append(final ILoggingEvent event) {
        if (bugsnag != null) {
            Throwable throwable = extractThrowable(event);

            // Only send a message if there is an exception
            if (throwable != null && !detectLogFromBugsnag(throwable)) {
                bugsnag.notify(
                        throwable,
                        calculateSeverity(event),
                        new Callback() {
                            @Override
                            public void beforeNotify(Report report) {

                                // Add some data from the logging event
                                report.addToTab("Log event data", "Message", event.getMessage());
                                report.addToTab("Log event data", "Timestamp", event.getTimeStamp());

                                // Add details from the logging context to the event
                                populateContextData(report, event);
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
     * Checks to see if a stack trace came from the Bugsnag library (prevent possible infinite reporting loops)
     * @param throwable the exception to check
     * @return true if the stacktrace contains a frame from the Bugsnag library
     */
    private boolean detectLogFromBugsnag(Throwable throwable) {

        // Check all places that LOGGER is called with an exception in the Bugsnag library
        for (StackTraceElement element : throwable.getStackTrace()) {
            if (element.getClassName().startsWith("com.bugsnag.Bugsnag")
                    || element.getClassName().startsWith("com.bugsnag.delivery.OutputStreamDelivery")
                    || element.getClassName().startsWith("com.bugsnag.delivery.SyncHttpDelivery")) {
                return true;
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
            return ((ThrowableProxy) event.getThrowableProxy()).getThrowable();
        }

        return null;
    }

    /**
     * @return Create a Bugsnag instance with parameters from the logback configuration
     */
    private Bugsnag createBugsnag() {
        Bugsnag bugsnag = Bugsnag.createBugsnag(apiKey);

        if (appType != null) {
            bugsnag.setAppType(appType);
        }

        if (appVersion != null) {
            bugsnag.setAppVersion(appVersion);
        }

        if (notifyEndpoint != null) {
            bugsnag.setEndpoints(notifyEndpoint, sessionEndpoint);
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

        return bugsnag;
    }

    /**
     * Adds the given key / value to the current thread logging context
     *
     * @param key the key to add
     * @param value the value to add
     */
    public static void addThreadMetaData(String tab, String key, String value) {
        MDC.put(LOGGING_CONTEXT_THREAD_PREFIX + tab + LOGGING_CONTEXT_TAB_SEPARATOR + key, value);
    }

    /**
     * Adds the given key / value to the current thread logging context, to be used for the next report
     *
     * @param key the key to add
     * @param value the value to add
     */
    public static void addReportMetaData(String tab, String key, String value) {
        MDC.put(LOGGING_CONTEXT_REPORT_PREFIX + tab + LOGGING_CONTEXT_TAB_SEPARATOR + key, value);
    }

    /**
     * Adds thread logging context values to the given report meta data
     *
     * @param report The report being sent to Bugsnag
     * @param event The values in the logging context
     */
    private void populateContextData(Report report, ILoggingEvent event) {
        List<String> keysToRemove = new ArrayList<String>();

        // Loop through all the keys and put them in the correct tabs
        for (String key : event.getMDCPropertyMap().keySet()) {
            if (key.startsWith(LOGGING_CONTEXT_REPORT_PREFIX)) {
                populateKey(key, event.getMDCPropertyMap().get(key), LOGGING_CONTEXT_REPORT_PREFIX,  report);
                keysToRemove.add(key);
            } else if (key.startsWith(LOGGING_CONTEXT_THREAD_PREFIX)) {
                populateKey(key, event.getMDCPropertyMap().get(key), LOGGING_CONTEXT_THREAD_PREFIX, report);
            }
        }

        // Remove the report keys so that they won't be associated with any other log message
        for (String key : keysToRemove) {
            event.getMDCPropertyMap().remove(key);
        }
    }

    /**
     * Clears all meta data added to the current thread
     */
    public static void clearThreadMetaData() {
        List<String> keysToRemove = new ArrayList<String>();

        // Loop through all the keys and collect the thread ones
        for (String key : MDC.getCopyOfContextMap().keySet()) {
            if (key.startsWith(LOGGING_CONTEXT_THREAD_PREFIX)) {
                keysToRemove.add(key);
            }
        }

        // Remove the keys
        for (String key : keysToRemove) {
            MDC.remove(key);
        }
    }

    /**
     * Adds the given key/value to the report
     *
     * @param key    The key to add
     * @param value  The value to add
     * @param prefix The prefix of the key
     * @param report The report to add the value to
     */
    private void populateKey(String key, String value, String prefix, Report report) {
        if (key.contains(LOGGING_CONTEXT_TAB_SEPARATOR)) {
            String[] parts = key
                    .substring(prefix.length())
                    .split(LOGGING_CONTEXT_TAB_SEPARATOR);

            report.addToTab(parts[0], parts[1], value);
        } else {
            report.addToTab("Context Data", key, value);
        }
    }


    // Setters

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setAppType(String appType) {
        this.appType = appType;

        if (bugsnag != null) {
            bugsnag.setAppType(appType);
        }
    }

    public void setCallback(Callback callback) {
        if (bugsnag != null) {
            bugsnag.addCallback(callback);
        }
    }

    public void setNotifyEndpoint(String notifyEndpoint) {
        this.notifyEndpoint = notifyEndpoint;

        if (bugsnag != null) {
            bugsnag.setEndpoint(notifyEndpoint);
        }
    }

    public void setSessionEndpoint(String sessionEndpoint) {
        this.sessionEndpoint = sessionEndpoint;

        if (bugsnag != null) {
            bugsnag.setSessionEndpoint(sessionEndpoint);
        }
    }

    public void setFilteredProperties(String filters) {
        this.filteredProperties.addAll(split(filters));

        if (bugsnag != null) {
            bugsnag.setFilters(filters);
        }
    }

    public void setIgnoredClasses(String ignoredClasses) {
        this.ignoredClasses.addAll(split(ignoredClasses));

        if (bugsnag != null) {
            bugsnag.setIgnoreClasses(ignoredClasses);
        }
    }

    public void setNotifyReleaseStages(String notifyReleaseStages) {
        this.notifyReleaseStages.addAll(split(notifyReleaseStages));

        if (bugsnag != null) {
            bugsnag.setNotifyReleaseStages(notifyReleaseStages);
        }
    }

    public void setProjectPackages(String projectPackages) {
        this.projectPackages.addAll(split(projectPackages));

        if (bugsnag != null) {
            bugsnag.setProjectPackages(projectPackages);
        }
    }

    public void setProxy(ProxyConfiguration proxy) {
        this.proxy = proxy;

        if (bugsnag != null) {
            bugsnag.setProxy(
                    new Proxy(
                            proxy.getType(),
                            new InetSocketAddress(proxy.getHostname(), proxy.getPort())));
        }
    }

    public void setReleaseStage(String releaseStage) {
        this.releaseStage = releaseStage;

        if (bugsnag != null) {
            bugsnag.setReleaseStage(releaseStage);
        }
    }

    public void setSendThreads(boolean sendThreads) {
        this.sendThreads = sendThreads;

        if (bugsnag != null) {
            bugsnag.setSendThreads(sendThreads);
        }
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;

        if (bugsnag != null) {
            bugsnag.setTimeout(timeout);
        }
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;

        if (bugsnag != null) {
            bugsnag.setAppVersion(appVersion);
        }
    }

    private List<String> split(String value) {
        String[] parts = value.split(",", -1);
        return Arrays.asList(parts);
    }

    Bugsnag getBugsnag() {
        return bugsnag;
    }
}