package com.bugsnag.mazerunnerspringboot;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    private static final Logger LOGGER = Logger.getLogger(ScheduledTaskService.class);

    private volatile boolean throwException = false;

    private static ScheduledTaskService instance;
    public ScheduledTaskService() {
        instance = this;
    }

    public static void setThrowException() {
        instance.throwException = true;
    }

    @Scheduled(fixedDelay=3000)
    public void doSomething() {
        if (throwException) {
            throw new RuntimeException("Unhandled exception from ScheduledTaskService");
        }
    }
}
