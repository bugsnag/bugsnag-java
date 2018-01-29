package com.bugsnag;

import java.util.Date;
import java.util.UUID;

class SessionTracker {

    private final Configuration configuration;

    SessionTracker(Configuration configuration) {
        this.configuration = configuration;
    }

    void startNewSession(Date date, boolean autoCaptured) {
        if (!configuration.shouldNotifyForReleaseStage()) {
            return;
        }

        new Session(UUID.randomUUID().toString(), date);
        // TODO
    }

}