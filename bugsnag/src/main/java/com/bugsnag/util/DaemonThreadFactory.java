package com.bugsnag.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Wraps {@link Executors#defaultThreadFactory()} to return daemon threads
 * This is to prevent applications from hanging waiting for the sessions scheduled task
 * because daemon threads will be terminated on application shutdown
 */
public class DaemonThreadFactory implements ThreadFactory {
    private final ThreadFactory defaultThreadFactory;

    /**
     * Constructor
     */
    public DaemonThreadFactory() {
        defaultThreadFactory = Executors.defaultThreadFactory();
    }

    @Override
    public Thread newThread(Runnable runner) {
        Thread daemonThread = defaultThreadFactory.newThread(runner);
        daemonThread.setName("bugsnag-daemon-" + daemonThread.getId());

        // Set the threads to daemon to allow the app to shutdown properly
        if (!daemonThread.isDaemon()) {
            daemonThread.setDaemon(true);
        }
        return daemonThread;
    }
}
