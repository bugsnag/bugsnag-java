package com.bugsnag.callbacks;

import com.bugsnag.Report;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DeviceCallback implements Callback {

    private static volatile String hostname;
    private static volatile boolean hostnameInitialised;
    private static final Object LOCK = new Object();
    private static final int HOSTNAME_LOOKUP_TIMEOUT = 10000;

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

        // Resort to dns hostname lookup.
        // Look up the hostname in a daemon thread, with a timeout of HOSTNAME_LOOKUP_TIMEOUT.
        // This is to prevent applications hanging waiting for the dns lookup to resolve
        FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws UnknownHostException {
                return InetAddress.getLocalHost().getHostName();
            }
        });
        Thread resolverThread = new Thread(future, "Hostname Resolver");
        resolverThread.setDaemon(true);
        resolverThread.start();

        try {
            return future.get(HOSTNAME_LOOKUP_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (ExecutionException ex) {
            // Give up
        } catch (InterruptedException ex) {
            // Give up
        } catch (TimeoutException ex) {
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
                .addToTab("device", "osArch", System.getProperty("os.arch"))
                .addToTab("device", "locale", Locale.getDefault())
                .setDeviceInfo("hostname", getHostnameValue())
                .setDeviceInfo("osName", System.getProperty("os.name"))
                .setDeviceInfo("osVersion", System.getProperty("os.version"));
    }
}
