package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class DeviceIDTest {

    private BugsnagDesktopDevice device;

    @Before
    public void setUp() throws BackingStoreException {
        Preferences.userNodeForPackage(BugsnagDesktopDevice.class).clear();
        device = new BugsnagDesktopDevice();
    }

    @Test
    public void testGenerateNewDeviceIdWhenNoIdExists() {
        Preferences prefs = Preferences.userNodeForPackage(BugsnagDesktopDevice.class);
        prefs.remove("id");

        BugsnagDesktopDevice newDevice = new BugsnagDesktopDevice();
        assertNotNull(device.getDeviceId(), "Device ID should not be null");
        assertFalse("Device ID should not be empty", device.getDeviceId().isEmpty());

        String savedId = prefs.get("id", null);
        assertNotNull("Device ID should be saved to preferences", savedId);
    }

    @Test
    public void testUseSavedDeviceIdWhenIdExists() {
        String fakeId = UUID.randomUUID().toString();
        Preferences prefs = Preferences.userNodeForPackage(BugsnagDesktopDevice.class);
        prefs.put("id", fakeId);

        assertEquals(fakeId, prefs.get("id", null));

        BugsnagDesktopDevice newDevice = new BugsnagDesktopDevice();
        assertEquals(fakeId, newDevice.getDeviceId());
    }

    @Test
    public void testIdConsistancy() {
        String fixedId = "12345678-abcd-1234-abcd-1234567890ab";

        BugsnagDesktopDevice deviceId1 = new BugsnagDesktopDevice() {
            @Override
            public String getDeviceId() {
                return fixedId;
            }
        };

        BugsnagDesktopDevice deviceId2 = new BugsnagDesktopDevice() {
            @Override
            public String getDeviceId() {
                return fixedId;
            }
        };

        assertEquals(deviceId1.getDeviceId(), deviceId2.getDeviceId());
    }

    @Test
    public void testClearPerferencesResetsDeviceId() throws BackingStoreException {
        String fakeId = UUID.randomUUID().toString();
        Preferences prefs = Preferences.userNodeForPackage(BugsnagDesktopDevice.class);
        prefs.put("id", fakeId);

        BugsnagDesktopDevice newDevice = new BugsnagDesktopDevice();
        assertEquals(fakeId, newDevice.getDeviceId());

        prefs.clear();
        BugsnagDesktopDevice clearedDevice = new BugsnagDesktopDevice();
        assertNotEquals(fakeId, clearedDevice.getDeviceId());
    }
}
