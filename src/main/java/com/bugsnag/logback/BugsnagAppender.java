package com.bugsnag.logback;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Sends events to Bugsnag using its Java client library. */
public class BugsnagAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    /** Bugsnag API key; the appender doesn't do anything if it's not available. */
    private String apiKey;
    /** Application type. */
    private String appType;
    /** Callback that can be used to enhance the report before sending it to Bugsnag servers. */
    private LogEventAwareCallback callback;
    /** Bugsnag server endpoint. */
    private String endpoint;
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

    @Override
    public void start() {
        if (apiKey != null && !apiKey.isEmpty()) {
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
    protected void append(final ILoggingEvent event) {
        if (bugsnag != null) {
            Throwable throwable = extractThrowable(event);
            if (callback == null) {
                bugsnag.notify(throwable);
            } else {
                bugsnag.notify(
                        throwable,
                        new Callback() {
                            @Override
                            public void beforeNotify(Report report) {
                                callback.beforeNotify(report, event);
                            }
                        });
            }
        }
    }

    private Throwable extractThrowable(ILoggingEvent event) {
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy instanceof ThrowableProxy) {
            return ((ThrowableProxy) event.getThrowableProxy()).getThrowable();
        }

        return new ThrowableNotAvailableException(event.getFormattedMessage());
    }

    private Bugsnag createBugsnag() {
        Bugsnag bugsnag = new Bugsnag(apiKey);

        if (appType != null) {
            bugsnag.setAppType(appType);
        }

        if (version != null) {
            bugsnag.setAppVersion(version);
        }

        if (endpoint != null) {
            bugsnag.setEndpoint(endpoint);
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

    private List<String> split(String value) {
        String[] parts = value.split(",", -1);
        return Arrays.asList(parts);
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

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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
}
