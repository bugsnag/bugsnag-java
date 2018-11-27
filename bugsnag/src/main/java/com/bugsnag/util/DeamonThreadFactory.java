package com.bugsnag.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A version of {@link Executors#defaultThreadFactory()} which returns deamon threads
 * This is to prevent applications from hanging waiting for the sessions scheduled task
 * because deamon threads will be terminated on application shutdown
 */
public class DeamonThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    /**
     * Constructor
     */
    public DeamonThreadFactory() {
        SecurityManager manager = System.getSecurityManager();
        if (manager != null) {
            group = manager.getThreadGroup();
        } else {
            group = Thread.currentThread().getThreadGroup();
        }

        namePrefix = "deamon-pool-"
                + poolNumber.getAndIncrement()
                + "-thread-";
    }

    /**
     * @see ThreadFactory#newThread(Runnable)
     */
    public Thread newThread(Runnable runner) {
        Thread deamonThread = new Thread(group,
                runner,
                namePrefix + threadNumber.getAndIncrement(),
                0);

        // Set the threads to deamon to allow the app to shutdown properly
        if (!deamonThread.isDaemon()) {
            deamonThread.setDaemon(true);
        }

        if (deamonThread.getPriority() != Thread.NORM_PRIORITY) {
            deamonThread.setPriority(Thread.NORM_PRIORITY);
        }

        return deamonThread;
    }
}
