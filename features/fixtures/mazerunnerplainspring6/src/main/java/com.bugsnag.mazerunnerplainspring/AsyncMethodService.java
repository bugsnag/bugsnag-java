package com.bugsnag.mazerunnerplainspring;

import com.bugsnag.Bugsnag;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AsyncMethodService {

    @Async
    public void doSomethingAsync() {

        // Add some thread metadata
        Bugsnag.addThreadMetadata("thread", "key1", "should be cleared from metadata");
        Bugsnag.clearThreadMetadata();
        Bugsnag.addThreadMetadata("thread", "key2", "should be included in metadata");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        throw new RuntimeException("Unhandled exception from Async method");
    }
}
