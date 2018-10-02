package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Collections;
import java.util.List;

class Notification {
    private Configuration config;
    private Report report;

    Notification(Configuration config, Report report) {
        this.config = config;
        this.report = report;
    }

    @Expose
    public String getApiKey() {
        String reportApiKey = report.getApiKey();
        return reportApiKey != null ? reportApiKey : config.apiKey;
    }

    @Expose
    public Notifier getNotifier() {
        return new Notifier();
    }

    @Expose
    public List<Report> getEvents() {
        return Collections.singletonList(report);
    }
}
