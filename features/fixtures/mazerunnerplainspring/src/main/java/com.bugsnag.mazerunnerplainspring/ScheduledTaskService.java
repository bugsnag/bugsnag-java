package com.bugsnag.mazerunnerplainspring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Value("${RUN_SCHEDULED_TASK:false}")
    private boolean throwException;

    private volatile boolean exceptionSent = false;

    @Scheduled(fixedDelay = 3000)
    public void doSomething() {
        if (throwException && !exceptionSent) {

            // Add some thread meta data
            Bugsnag.addThreadMetaData("thread", "key1", "should be cleared from meta data");
            Bugsnag.clearThreadMetaData();
            Bugsnag.addThreadMetaData("thread", "key2", "should be included in meta data");

            exceptionSent = true;
            throw new RuntimeException("Unhandled exception from ScheduledTaskService");
        }
    }
}
