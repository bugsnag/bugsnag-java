package com.bugsnag;

import com.bugsnag.serialization.Expose;

class Notifier {
    private static final String NOTIFIER_NAME = "Bugsnag Java";
    private static final String NOTIFIER_VERSION = "2.0.0";
    private static final String NOTIFIER_URL = "https://github.com/bugsnag/bugsnag-java";

    @Expose
    public String getName() {
        return NOTIFIER_NAME;
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
