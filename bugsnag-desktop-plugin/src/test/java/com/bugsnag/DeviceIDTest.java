package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.beans.Transient;
import java.util.UUID;
import java.util.prefs.Preferences;


public class DeviceIDTest {

    private BugsnagDesktopDevice device;

    @Before
    public void setUp() { 
        Preferences.userNodeForPackage(BugsnagDesktopDevice.class).clear();
        device = new BugsnagDesktopDevice();
    }

    @Test
    public testGenerateNewDeviceIdWhenNoIdExists() {
        Preferences prefs = Preferences.userNodeForPackage(BugsnagDesktopDevice.class);
        prefs.remove("id");

        BugsnagDesktopDevice newDevice = new BugsnagDesktopDevice();
        assertNotNull(device.getDeviceId(), "Device ID should not be null");
        assertFalse(device.getDeviceId().isEmpty(), "Device ID should not be empty");

        String savedId = prefs.get("id", null);
        assertNotNull(newDevice.getDeviceId(), savedId, "Device ID should be saved to preferences");
    }

    @Test
    public void testUseSavedDeviceIdWhenIdExists() {
        String fakeId = UUID.randomUUID().toString();
        Preferences prefs = Preferences.userNodeForPackage(BugsnagDesktopDevice.class);
        prefs.put("id", fakeId);

        BugsnagDesktopDevice newDevice = new BugsnagDesktopDevice();
        assertEquals(fakeId, newDevice.getDeviceId(), "Device ID should be loaded from preferences");
    }
    
    @Test
    public void testIdConsistancy() {
        String deviceId1 = device.getDeviceId();
        String deviceId2 = new BugsnagDesktopDevice().getDeviceId();

        assertEquals(deviceId1, deviceId2, "Device ID should be consistent between instances");
    }

    @Test
    public void testClearPerferencesResetsDeviceId() {
        String fakeId = UUID.randomUUID().toString();
        Preferences prefs = Preferences.userNodeForPackage(BugsnagDesktopDevice.class);
        prefs.put("id", fakeId);

        BugsnagDesktopDevice newDevice = new BugsnagDesktopDevice();
        assertEquals(fakeId, newDevice.getDeviceId(), "Device ID should be loaded from preferences");

        prefs.clear();
        BugsnagDesktopDevice clearedDevice = new BugsnagDesktopDevice();
        assertNotEquals(fakeId, clearedDevice.getDeviceId(), "Device ID should be reset after clearing preferences");
    }
}
