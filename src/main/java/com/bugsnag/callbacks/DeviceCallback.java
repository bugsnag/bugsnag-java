package com.bugsnag.callbacks;

import com.bugsnag.Report;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

public class DeviceCallback implements Callback {
    @Override
    public void beforeNotify(Report report) {
        report
            .setDeviceInfo("hostname", getHostname())
            .setDeviceInfo("osName", System.getProperty("os.name"))
            .setDeviceInfo("osVersion", System.getProperty("os.version"))
            .setDeviceInfo("osArch", System.getProperty("os.arch"))
            .setDeviceInfo("runtimeName", System.getProperty("java.runtime.name"))
            .setDeviceInfo("runtimeVersion", System.getProperty("java.runtime.version"))
            .setDeviceInfo("locale", Locale.getDefault());
    }

    private String getHostname() {
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
}
