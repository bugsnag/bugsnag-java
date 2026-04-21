package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    private volatile boolean throwException = false;

    private static ScheduledTaskService instance;

    public ScheduledTaskService() {
        instance = this;
    }

    public static void setThrowException() {
        instance.throwException = true;
    }

    @Scheduled(fixedDelay = 3000)
    public void doSomething() {
        if (throwException) {
            // Reset the flag so we only throw once
            throwException = false;

            // Add some thread metadata
            Bugsnag.addThreadMetadata("thread", "key1", "should be cleared from metadata");
            Bugsnag.clearThreadMetadata();
            Bugsnag.addThreadMetadata("thread", "key2", "should be included in metadata");

            throw new RuntimeException("Unhandled exception from ScheduledTaskService");
        }
    }
}

