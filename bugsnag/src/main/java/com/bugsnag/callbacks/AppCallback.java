package com.bugsnag.callbacks;

import com.bugsnag.BugsnagEvent;
import com.bugsnag.Configuration;

public class AppCallback implements OnErrorCallback {
    private Configuration config;

    public AppCallback(Configuration config) {
        this.config = config;
    }

    @Override
    public boolean onError(BugsnagEvent event) {
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
