package com.bugsnag.logback;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/** Sends events to Bugsnag using its Java client library. */
public class BugsnagAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    /** Bugsnag API key; the appender doesn't do anything if it's not available. */
    private String apiKey;
    /** Bugsnag app type. */
    private String appType;
    /** Callback that can be used to enhance the report before sending it to Bugsnag servers. */
    private LogEventAwareCallback callback;
    /** Bugsnag endpoint. */
    private String endpoint;
    /** Property names that should be filtered before sending to Bugsnag servers. */
    private String filters;
    /** Exception classes to be ignored. */
    private String ignoreClasses;
    /** Release stages that should be notified. */
    private String notifyReleaseStages;
    /** Bugsnag Java packages configuration, separated by commas. */
    private String projectPackages;
    /** Bugsnag release stage in Bugsnag. */
    private String releaseStage;
    /** Whether threads state should be sent to Bugsnag. */
    private boolean sendThreads;
    /** Bugsnag api request timeout. */
    private int timeout;
    /** Application version, used to determine the app version in Bugsnag. */
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
            if (callback == null) {
                bugsnag.notify(extractThrowable(event));
            } else {
                bugsnag.notify(
                        extractThrowable(event),
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
        checkRequiredParameter("api key", apiKey);
        checkRequiredParameter("app type", appType);
        checkRequiredParameter("release stage", releaseStage);
        checkRequiredParameter("version", version);

        if (getStatusManager().getCount() > 0) {
            return null;
        }

        Bugsnag bugsnag = new Bugsnag(apiKey);
        bugsnag.setAppType(appType);
        bugsnag.setAppVersion(version);

        if (endpoint != null) {
            bugsnag.setEndpoint(endpoint);
        }

        if (filters != null) {
            String[] filterNames = filters.split(",", -1);
            bugsnag.setFilters(filterNames);
        }

        if (ignoreClasses != null) {
            String[] classes = ignoreClasses.split(",", -1);
            bugsnag.setIgnoreClasses(classes);
        }

        if (notifyReleaseStages != null) {
            String[] notifyReleaseStageNames = notifyReleaseStages.split(",", -1);
            bugsnag.setNotifyReleaseStages(notifyReleaseStageNames);
        }

        if (projectPackages != null) {
            String[] packages = projectPackages.split(",", -1);
            bugsnag.setProjectPackages(packages);
        }

        if (timeout > 0) {
            bugsnag.setTimeout(timeout);
        }

        bugsnag.setSendThreads(sendThreads);
        bugsnag.setReleaseStage(releaseStage);

        return bugsnag;
    }

    private void checkRequiredParameter(String parameterName, String parameterValue) {
        if (parameterValue == null || parameterValue.isEmpty()) {
            addError(
                    String.format(
                            "No %s set for the appender named [%s]", parameterName, this.name));
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

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public void setIgnoreClasses(String ignoreClasses) {
        this.ignoreClasses = ignoreClasses;
    }

    public void setNotifyReleaseStages(String notifyReleaseStages) {
        this.notifyReleaseStages = notifyReleaseStages;
    }

    public void setProjectPackages(String projectPackages) {
        this.projectPackages = projectPackages;
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
