package com.bugsnag;

import java.util.UUID;
import java.util.prefs.Preferences;

public class BugsnagDesktopDevice {
    private static final String DEVICE_ID_KEY = "id";
    private static final Preferences PREFS = Preferences.userNodeForPackage(BugsnagDesktopDevice.class);

    private final String deviceId;

    public BugsnagDesktopDevice() {
        deviceId = generateDeviceId();
    }

    private String generateDeviceId() {
        String id = PREFS.get(DEVICE_ID_KEY, null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            PREFS.put(DEVICE_ID_KEY, id);
        }
        return id;
    }

    public  String getDeviceId() {
        return deviceId;
    }
}
