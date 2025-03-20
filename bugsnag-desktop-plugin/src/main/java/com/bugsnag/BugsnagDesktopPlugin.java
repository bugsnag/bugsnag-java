package com.bugsnag;

public class BugsnagDesktopPlugin{

    private Bugsnag bugsnag;
    BugsnagDesktopDevice DeviceIDManager = new BugsnagDesktopDevice();

    public BugsnagDesktopPlugin(Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    public void initialize()
    {
        bugsnag.setAutoCaptureSessions(true);
        bugsnag.startSession();

        this.addDeviceIdToSessions();
        this.addDeviceIdToEvents();
    }

    private void addDeviceIdToSessions() { 
        this.bugsnag.addBeforeSendSession(session -> { 
            session.getDevice().put("id", getDeviceId());
        });
    }

    private void addDeviceIdToEvents() {
        this.bugsnag.addCallback(event -> {
            event.getDevice().put("id", getDeviceId());
        });
    }

    private String getDeviceId() {
        // Implement logic to retrieve or generate a unique device ID
        return DeviceIDManager.getDeviceId();
    }
}

