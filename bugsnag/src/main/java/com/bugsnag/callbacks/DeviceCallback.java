package com.bugsnag.callbacks;

import com.bugsnag.Report;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

public class DeviceCallback implements Callback {

    private static volatile String hostname;
    private static transient volatile boolean hostnameInitialised;
    private static final Object LOCK = new Object();

    /**
     * Memoises the hostname, as lookup can be expensive
     */
    public static String getHostnameValue() {
        if (!hostnameInitialised) {
            synchronized (LOCK) {
                if (!hostnameInitialised) {
                    hostname = lookupHostname();
                    hostnameInitialised = true;
                }
            }
        }
        return hostname;
    }

    private static String lookupHostname() {
        // Windows always sets COMPUTERNAME
        if (System.getProperty("os.name").startsWith("Windows")) {
            return System.getenv("COMPUTERNAME");
        }

        // Try the HOSTNAME env variable (most unix systems)
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null) {
            return hostname;
        }

        // Resort to dns hostname lookup
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            // Give up
        }
        return null;
    }

    public static void initializeCache() {
        getHostnameValue();
    }

    @Override
    public void beforeNotify(Report report) {
        report
                .setDeviceInfo("hostname", getHostnameValue())
                .setDeviceInfo("osName", System.getProperty("os.name"))
                .setDeviceInfo("osVersion", System.getProperty("os.version"))
                .setDeviceInfo("osArch", System.getProperty("os.arch"))
                .setDeviceInfo("runtimeName", System.getProperty("java.runtime.name"))
                .setDeviceInfo("runtimeVersion", System.getProperty("java.runtime.version"))
                .setDeviceInfo("locale", Locale.getDefault());
    }
}
