package com.bugsnag;

import com.bugsnag.http.NetworkException;

public class Client {
    protected Configuration config = new Configuration();
    protected Diagnostics diagnostics = new Diagnostics(config);

    public Client(String apiKey) {
        this(apiKey, true);
    }

    public Client(String apiKey, boolean installHandler) {
        if(apiKey == null) {
            throw new RuntimeException("You must provide a Bugsnag API key");
        }
        config.apiKey = apiKey;

        // Install a default exception handler with this client
        if(installHandler) {
            ExceptionHandler.install(this);
        }
    }

    public Client setContext(String context) {
        config.context.setLocked(context);
        return this;
    }

    /**
    * @deprecated  Replaced by {@link #setUser(String, String, String)}}
    */
    public Client setUserId(String id) {
        config.setUser(id, null, null);
        return this;
    }

    public Client setUser(String id, String email, String name) {
        config.setUser(id, email, name);
        return this;
    }

    public Client setReleaseStage(String releaseStage) {
        config.releaseStage.setLocked(releaseStage);
        return this;
    }

    public Client setNotifyReleaseStages(String... notifyReleaseStages) {
        config.setNotifyReleaseStages(notifyReleaseStages);
        return this;
    }

    public Client setAutoNotify(boolean autoNotify) {
        config.setAutoNotify(autoNotify);
        return this;
    }

    public Client setUseSSL(boolean useSSL) {
        config.setUseSSL(useSSL);
        return this;
    }

    public boolean getUseSSL() {
        return config.useSSL;
    }

    public Client setEndpoint(String endpoint) {
        config.setEndpoint(endpoint);
        return this;
    }

    public Client setFilters(String... filters) {
        config.setFilters(filters);
        return this;
    }

    public Client setProjectPackages(String... projectPackages) {
        config.setProjectPackages(projectPackages);
        return this;
    }

    public Client setOsVersion(String osVersion) {
        config.osVersion.setLocked(osVersion);
        return this;
    }

    public Client setAppVersion(String appVersion) {
        config.appVersion.setLocked(appVersion);
        return this;
    }

    public Client setNotifierName(String notifierName) {
        config.setNotifierName(notifierName);
        return this;
    }

    public Client setNotifierVersion(String notifierVersion) {
        config.setNotifierVersion(notifierVersion);
        return this;
    }

    public Client setNotifierUrl(String notifierUrl) {
        config.setNotifierUrl(notifierUrl);
        return this;
    }

    public Client setIgnoreClasses(String... ignoreClasses) {
        config.setIgnoreClasses(ignoreClasses);
        return this;
    }

    public Client setLogger(Logger logger) {
        config.setLogger(logger);
        return this;
    }

    public Client setSendThreads(boolean sendThreads) {
        config.setSendThreads(sendThreads);
        return this;
    }

    public Client addBeforeNotify(BeforeNotify beforeNotify) {
        config.addBeforeNotify(beforeNotify);
        return this;
    }

    public void notify(Error error) {
        if(!config.shouldNotify()) return;
        if(error.shouldIgnore()) return;
        if(!beforeNotify(error)) return;

        try {
            Notification notif = new Notification(config, error);
            notif.deliver();
        } catch (NetworkException ex) {
            config.logger.warn("Error notifying Bugsnag", ex);
        }
    }

    public void notify(Throwable e, String severity, MetaData metaData) {
        Error error = new Error(e, severity, metaData, config, diagnostics);
        notify(error);
    }

    public void notify(Throwable e, MetaData metaData) {
        notify(e, null, metaData);
    }

    public void notify(Throwable e, String severity) {
        notify(e, severity, null);
    }

    public void notify(Throwable e) {
        notify(e, null, null);
    }

    public void autoNotify(Throwable e) {
        if(config.autoNotify) {
            notify(e, "error");
        }
    }

    public Client addToTab(String tab, String key, Object value) {
        config.addToTab(tab, key, value);
        return this;
    }

    public void clearTab(String tab) {
        config.clearTab(tab);
    }

    public void trackUser() {
        try {
            Metrics metrics = new Metrics(config, diagnostics);
            metrics.deliver();
        } catch (NetworkException ex) {
            config.logger.warn("Error sending metrics to Bugsnag", ex);
        }
    }

    protected boolean beforeNotify(Error error) {
        for (BeforeNotify beforeNotify : config.beforeNotify) {
            try {
                if (!beforeNotify.run(error)) {
                    return false;
                }
            } catch (Throwable ex) {
                config.logger.warn("BeforeNotify threw an Exception", ex);
            }
        }

        // By default, allow the error to be sent if there were no objections
        return true;
    }

    // Factory methods so we don't have to expose the Configuration class
    public Notification createNotification() {
        return new Notification(config);
    }

    public Notification createNotification(Error error) {
        return new Notification(config, error);
    }

    public Metrics createMetrics() {
        return new Metrics(config, diagnostics);
    }

    public Error createError(Throwable e, String severity, MetaData metaData) {
        return new Error(e, severity, metaData, config, diagnostics);
    }
}
