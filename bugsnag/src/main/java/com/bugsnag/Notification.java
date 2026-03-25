package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Collections;
import java.util.List;

class Notification {
    private Configuration config;
    private BugsnagEvent event;

    Notification(Configuration config, BugsnagEvent event) {
        this.config = config;
        this.event = event;
    }

    @Expose
    public String getApiKey() {
        String reportApiKey = event.getApiKey();
        return reportApiKey != null ? reportApiKey : config.getApiKey();
    }

    @Expose
    public Notifier getNotifier() {
        return NotifierUtils.getNotifier();
    }

    @Expose
    public List<BugsnagEvent> getEvents() {
        return Collections.singletonList(event);
    }
}
