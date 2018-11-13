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

        // Add some thread meta data
        Bugsnag.addThreadMetaData("thread", "key1", "should be cleared from meta data");
        Bugsnag.clearThreadMetaData();
        Bugsnag.addThreadMetaData("thread", "key2", "should be included in meta data");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        throw new RuntimeException("Unhandled exception from Async method");
    }
}
