package com.bugsnag.callbacks;

import com.bugsnag.Configuration;
import com.bugsnag.Report;

public class AppCallback implements Callback {
    private Configuration config;

    public AppCallback(Configuration config) {
        this.config = config;
    }

    @Override
    public void beforeNotify(Report report) {
        if (config.getAppType() != null) {
            report.setAppInfo("type", config.getAppType());
        }

        if (config.getAppVersion() != null) {
            report.setAppInfo("version", config.getAppVersion());
        }

        if (config.getReleaseStage() != null) {
            report.setAppInfo("releaseStage", config.getReleaseStage());
        }
    }
}
