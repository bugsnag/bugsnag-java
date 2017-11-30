package com.bugsnag.callbacks;

import com.bugsnag.Report;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

public class DeviceCallback implements Callback {
    private static final Supplier<String> hostnameCache =
        Suppliers.memoize(new Supplier<String>() {

            public String get() {
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
        });

    public static void initializeCache() {
        hostnameCache.get();
    }

    @Override
    public void beforeNotify(Report report) {
        report
                .setDeviceInfo("hostname", hostnameCache.get())
                .setDeviceInfo("osName", System.getProperty("os.name"))
                .setDeviceInfo("osVersion", System.getProperty("os.version"))
                .setDeviceInfo("osArch", System.getProperty("os.arch"))
                .setDeviceInfo("runtimeName", System.getProperty("java.runtime.name"))
                .setDeviceInfo("runtimeVersion", System.getProperty("java.runtime.version"))
                .setDeviceInfo("locale", Locale.getDefault());
    }
}
