package com.bugsnag.callbacks;

import com.bugsnag.Configuration;
import com.bugsnag.Event;

public class AppCallback implements Callback {
    private Configuration config;

    public AppCallback(Configuration config) {
        this.config = config;
    }

    @Override
    public boolean onError(Event event) {
        if (config.getAppType() != null) {
            event.setAppInfo("type", config.getAppType());
        }

        if (config.getAppVersion() != null) {
            event.setAppInfo("version", config.getAppVersion());
        }

        if (config.getReleaseStage() != null) {
            event.setAppInfo("releaseStage", config.getReleaseStage());
        }
        return true;
    }
}
