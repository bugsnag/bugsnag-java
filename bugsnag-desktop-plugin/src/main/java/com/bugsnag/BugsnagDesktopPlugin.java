package com.bugsnag;

import java.io.ObjectInputFilter.Config;

public class BugsnagDesktopPlugin implements Plugin {

    BugsnagDesktopDevice DeviceIDManager = new BugsnagDesktopDevice();
    private final Configuration config;
    private boolean pluginLoaded;

    public BugsnagDesktopPlugin(Configuration config) {
        this.config = config;
    }

    @Override
    public void load()
    {
        pluginLoaded = true;

        config.setAutoCaptureSessions(true);
        addDeviceIdToSessions();
        addDeviceIdToEvents();
    }
    
    @Override
    public void unload()
    {
        pluginLoaded = false;

    }

    private void addDeviceIdToSessions() { 
        this.config.addBeforeSendSession(session -> { 
            if(pluginLoaded) {
                session.getDevice().put("id", getDeviceId());
            }
        });
    }

    private void addDeviceIdToEvents() {
        this.config.addCallback(event -> {
            if(pluginLoaded) {
                event.getDevice().put("id", getDeviceId());
            }
        });
    }

    private String getDeviceId() {
        // Implement logic to retrieve or generate a unique device ID
        return DeviceIDManager.getDeviceId();
    }
}

