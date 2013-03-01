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
        config.setApiKey(apiKey);

        // Install a default exception handler with this client
        if(installHandler) {
            ExceptionHandler.install(this);
        }
    }

    public void setContext(String context) {
        config.setContext(context);
    }

    public void setUserId(String userId) {
        config.setUserId(userId);
    }

    public void setReleaseStage(String releaseStage) {
        config.setReleaseStage(releaseStage);
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

    public void setEndpoint(String endpoint) {
        config.setEndpoint(endpoint);
    }

    public void setFilters(String... filters) {
        config.setFilters(filters);
    }

    public void setProjectPackages(String... packages) {
        config.setProjectPackages(packages);
    }

    public void setOsVersion(String osVersion) {
        config.setOsVersion(osVersion);
    }

    public void setAppVersion(String appVersion) {
        config.setAppVersion(appVersion);
    }

    public void setLogger(Logger logger) {
        config.setLogger(logger);
    }

    public void notify(Throwable e, MetaData metaData) {
        if(!config.shouldNotify()) return;

        try {
            Notification notif = new Notification(config, new Error(e, metaData, config));
            notif.deliver();
        } catch (IOException ex) {
            config.getLogger().warn("Error notifying Bugsnag", ex);
        }
    }

    public void notify(Throwable e) {
        notify(e, null);
    }

    public void autoNotify(Throwable e) {
        if(config.getAutoNotify()) {
            notify(e);
        }
    }

    public void addToTab(String tab, String key, Object value) {
        config.addToTab(tab, key, value);
    }

    public void clearTab(String tab) {
        config.clearTab(tab);
    }
}