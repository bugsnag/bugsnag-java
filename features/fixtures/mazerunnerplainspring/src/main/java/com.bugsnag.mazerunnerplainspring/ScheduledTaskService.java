package com.bugsnag.mazerunnerplainspring;

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
            // Add some thread meta data
            Bugsnag.addThreadMetaData("thread", "key1", "should be cleared from meta data");
            Bugsnag.clearThreadMetaData();
            Bugsnag.addThreadMetaData("thread", "key2", "should be included in meta data");

            throw new RuntimeException("Unhandled exception from ScheduledTaskService");
        }
    }
}
