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

    public void setContext(String context) {
        config.context.setLocked(context);
    }

    /**
    * @deprecated  Replaced by {@link #setUser()}
    */
    public void setUserId(String id) {
        config.setUser(id, null, null);
    }

    public void setUser(String id, String email, String name) {
        config.setUser(id, email, name);
    }

    public void setReleaseStage(String releaseStage) {
        config.releaseStage.setLocked(releaseStage);
    }

    public void setNotifyReleaseStages(String... notifyReleaseStages) {
        config.setNotifyReleaseStages(notifyReleaseStages);
    }

    public void setAutoNotify(boolean autoNotify) {
        config.setAutoNotify(autoNotify);
    }

    public void setUseSSL(boolean useSSL) {
        config.setUseSSL(useSSL);
    }

    public boolean getUseSSL() {
        return config.useSSL;
    }

    public void setEndpoint(String endpoint) {
        config.setEndpoint(endpoint);
    }

    public void setFilters(String... filters) {
        config.setFilters(filters);
    }

    public void setProjectPackages(String... projectPackages) {
        config.setProjectPackages(projectPackages);
    }

    public void setOsVersion(String osVersion) {
        config.osVersion.setLocked(osVersion);
    }

    public void setAppVersion(String appVersion) {
        config.appVersion.setLocked(appVersion);
    }

    public void setNotifierName(String notifierName) {
        config.setNotifierName(notifierName);
    }

    public void setNotifierVersion(String notifierVersion) {
        config.setNotifierVersion(notifierVersion);
    }

    public void setNotifierUrl(String notifierUrl) {
        config.setNotifierUrl(notifierUrl);
    }

    public void setIgnoreClasses(String... ignoreClasses) {
        config.setIgnoreClasses(ignoreClasses);
    }

    public void setLogger(Logger logger) {
        config.setLogger(logger);
    }

    public void notify(Error error) {
        if(!config.shouldNotify()) return;
        if(error.shouldIgnore()) return;

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
            notify(e, "fatal");
        }
    }

    public void addToTab(String tab, String key, Object value) {
        config.addToTab(tab, key, value);
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