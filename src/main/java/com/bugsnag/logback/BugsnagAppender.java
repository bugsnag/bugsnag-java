package com.bugsnag.logback;

import ch.qos.logback.classic.Level;
import com.bugsnag.Bugsnag;
import com.bugsnag.Configuration;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.slf4j.MDC;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Sends events to Bugsnag using its Java client library. */
public class BugsnagAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    /** Bugsnag API key; the appender doesn't do anything if it's not available. */
    private String apiKey;

    /** Application type. */
    private String appType;

    /** Callback that can be used to enhance the report before sending it to Bugsnag servers. */
    private LogEventAwareCallback callback;

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
    private String version;

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

                                // Add details from the logging context to the event
                                populateContextData(report, event);

                                // Call the custom callback
                                if (callback != null) {
                                    callback.beforeNotify(report, event);
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

        if (version != null) {
            bugsnag.setAppVersion(version);
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

        bugsnag.setFilters(filteredProperties.toArray(new String[0]));
        bugsnag.setIgnoreClasses(ignoredClasses.toArray(new String[0]));
        bugsnag.setNotifyReleaseStages(notifyReleaseStages.toArray(new String[0]));
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
    public static void addToLoggingContext(String key, String value) {
        MDC.put(key, value);
    }

    /**
     * Adds thread logging context values to the given report meta data
     *
     * @param report The report being sent to Bugsnag
     * @param event The values in the logging context
     */
    private void populateContextData(Report report, ILoggingEvent event) {

        // TODO: add to specific tabs? set user information?
        report.addToTab("Log Message", "Message", event.getMessage());


        for (String key : event.getMDCPropertyMap().keySet()) {
            report.addToTab("Context Data", key, event.getMDCPropertyMap().get(key));
        }
    }

    // Setters

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setCallback(LogEventAwareCallback callback) {
        this.callback = callback;
    }

    public void setNotifyEndpoint(String notifyEndpoint) {
        this.notifyEndpoint = notifyEndpoint;
    }

    public void setSessionEndpoint(String sessionEndpoint) {
        this.sessionEndpoint = sessionEndpoint;
    }

    public void setFilteredProperties(String filters) {
        this.filteredProperties.addAll(split(filters));
    }

    public void addFilteredProperty(String filter) {
        this.filteredProperties.add(filter);
    }

    public void setIgnoredClasses(String ignoredClasses) {
        this.ignoredClasses.addAll(split(ignoredClasses));
    }

    public void addIgnoredClass(String ignoredClass) {
        this.ignoredClasses.add(ignoredClass);
    }

    public void setNotifyReleaseStages(String notifyReleaseStages) {
        this.notifyReleaseStages.addAll(split(notifyReleaseStages));
    }

    public void addNotifyReleaseStage(String notifyReleaseStage) {
        this.notifyReleaseStages.add(notifyReleaseStage);
    }

    public void setProjectPackages(String projectPackages) {
        this.projectPackages.addAll(split(projectPackages));
    }

    public void addProjectPackage(String projectPackage) {
        this.projectPackages.add(projectPackage);
    }

    public void setProxy(ProxyConfiguration proxy) {
        this.proxy = proxy;
    }

    public void setReleaseStage(String releaseStage) {
        this.releaseStage = releaseStage;
    }

    public void setSendThreads(boolean sendThreads) {
        this.sendThreads = sendThreads;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private List<String> split(String value) {
        String[] parts = value.split(",", -1);
        return Arrays.asList(parts);
    }

    public Bugsnag getBugsnag() {
        return bugsnag;
    }
}
