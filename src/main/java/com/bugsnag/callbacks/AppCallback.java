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
        if (config.appType != null) {
            report.setAppInfo("type", config.appType);
        }

        if (config.appVersion != null) {
            report.setAppInfo("version", config.appVersion);
        }

        if (config.releaseStage != null) {
            report.setAppInfo("releaseStage", config.releaseStage);
        }
    }
}
