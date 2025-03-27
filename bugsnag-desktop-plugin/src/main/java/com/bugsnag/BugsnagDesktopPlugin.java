package com.bugsnag;

public class BugsnagDesktopPlugin implements Plugin {

    private final Configuration config;
    private final BugsnagDesktopDevice deviceIDManager = new BugsnagDesktopDevice();
    private Bugsnag bugsnag;
    private boolean pluginLoaded = true;

    public BugsnagDesktopPlugin(Configuration config) {
        this.config = config;
    }

    @Override
    public void load(Bugsnag bugsnag) {
        this.bugsnag = bugsnag;

        bugsnag.setAutoCaptureSessions(true);
        addDeviceIdToSessions();
        addDeviceIdToEvents();
    }

    @Override
    public void unload() {
        pluginLoaded = false;
    }

    private void addDeviceIdToSessions() {
        bugsnag.addBeforeSendSession(session -> {
            if (pluginLoaded) {
                session.getDevice().put("id", getDeviceId());
            }
        });
    }

    private void addDeviceIdToEvents() {
        bugsnag.addCallback(event -> {
            if (pluginLoaded) {
                event.getDevice().put("id", getDeviceId());
            }
        });
    }

    private String getDeviceId() {
        // Implement logic to retrieve or generate a unique device ID
        return deviceIDManager.getDeviceId();
    }
}
