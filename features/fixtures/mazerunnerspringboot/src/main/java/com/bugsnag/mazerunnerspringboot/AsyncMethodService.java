package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
        } catch (InterruptedException ex) {
            // ignore
        }

        throw new RuntimeException("Unhandled exception from Async method");
    }
}
