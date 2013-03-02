package com.bugsnag;

import java.io.IOException;

public class Client {
    protected Configuration config = new Configuration();

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
        config.context = context;
    }

    public void setUserId(String userId) {
        config.userId = userId;
    }

    public void setReleaseStage(String releaseStage) {
        config.releaseStage = releaseStage;
    }

    public void setNotifyReleaseStages(String... notifyReleaseStages) {
        config.notifyReleaseStages = notifyReleaseStages;
    }

    public void setAutoNotify(boolean autoNotify) {
        config.autoNotify = autoNotify;
    }

    public void setUseSSL(boolean useSSL) {
        config.useSSL = useSSL;
    }

    public boolean getUseSSL() {
        return config.useSSL;
    }

    public void setEndpoint(String endpoint) {
        config.endpoint = endpoint;
    }

    public void setFilters(String... filters) {
        config.filters = filters;
    }

    public void setProjectPackages(String... projectPackages) {
        config.projectPackages = projectPackages;
    }

    public void setOsVersion(String osVersion) {
        config.osVersion = osVersion;
    }

    public void setAppVersion(String appVersion) {
        config.appVersion = appVersion;
    }

    public void setNotifierName(String notifierName) {
        config.notifierName = notifierName;
    }

    public void setNotifierVersion(String notifierVersion) {
        config.notifierVersion = notifierVersion;
    }

    public void setNotifierUrl(String notifierUrl) {
        config.notifierUrl = notifierUrl;
    }

    public void setLogger(Logger logger) {
        config.logger = logger;
    }

    public void notify(Throwable e, MetaData metaData) {
        if(!config.shouldNotify()) return;

        try {
            Notification notif = new Notification(config, new Error(e, metaData, config));
            notif.deliver();
        } catch (IOException ex) {
            config.logger.warn("Error notifying Bugsnag", ex);
        }
    }

    public void notify(Throwable e) {
        notify(e, null);
    }

    public void autoNotify(Throwable e) {
        if(config.autoNotify) {
            notify(e);
        }
    }

    public void addToTab(String tab, String key, Object value) {
        config.addToTab(tab, key, value);
    }

    public void clearTab(String tab) {
        config.clearTab(tab);
    }

    public Notification createNotification() {
        return new Notification(config);
    }

    public Notification createNotification(Error error) {
        return new Notification(config, error);
    }

    public Error createError(Throwable e, MetaData metaData) {
        return new Error(e, metaData, config);
    }
}