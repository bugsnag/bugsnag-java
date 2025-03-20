package com.bugsnag;

public class BugsnagDesktopConfiguration extends Configuration{
    
    private final BugsnagDesktopDevice deviceIdManager;

    BugsnagDesktopConfiguration(String apiKey) {
        super(apiKey);
        this.deviceIdManager = new BugsnagDesktopDevice();
    }

    public BugsnagDesktopDevice getDeviceIdManager() {
        return deviceIdManager;
    }
}
