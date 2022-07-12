package com.bugsnag;

import com.bugsnag.serialization.Expose;

class Notifier {

    private static final String NOTIFIER_NAME = "Bugsnag Java";
    private static final String NOTIFIER_VERSION = "3.6.4";
    private static final String NOTIFIER_URL = "https://github.com/bugsnag/bugsnag-java";

    private String notifierName = NOTIFIER_NAME;

    void setNotifierName(String notifierName) {
        this.notifierName = notifierName;
    }

    @Expose
    public String getName() {
        return notifierName;
    }

    @Expose
    public String getVersion() {
        return NOTIFIER_VERSION;
    }

    @Expose
    public String getUrl() {
        return NOTIFIER_URL;
    }
}
