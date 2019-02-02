package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

@Service
public class ScheduledTaskExecutorService {

    @Autowired
    private Bugsnag bugsnag;

    private final Set<String> threadNames = new HashSet<String>();

    private volatile boolean sendException = false;

    private static ScheduledTaskExecutorService instance;

    public ScheduledTaskExecutorService() {
        instance = this;
    }

    public static void setSendException() {
        instance.sendException = true;
    }

    public static Collection<String> getThreadNames() {
        return new HashSet<>(instance.threadNames);
    }

    @Scheduled(fixedRate = 100)
    public void doSomething() {
        if (sendException) {
            threadNames.add(Thread.currentThread().getName());
        }
    }
}
