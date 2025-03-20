package com.bugsnag;

import java.util.Date;

public class BugsnagDesktopSession extends Session{
    private String deviceId;

    public BugsnagDesktopSession(String id, Date startedAt, String deviceId) {
        super(id, startedAt);
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}

