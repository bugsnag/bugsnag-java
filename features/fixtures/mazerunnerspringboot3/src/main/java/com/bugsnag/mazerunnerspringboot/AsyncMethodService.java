package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncMethodService {

    @Autowired
    Bugsnag bugsnag;

    @Async
    public void doSomethingAsync() {

        // Add some thread metadata
        Bugsnag.addThreadMetadata("thread", "key1", "should be cleared from metadata");
        Bugsnag.clearThreadMetadata();
        Bugsnag.addThreadMetadata("thread", "key2", "should be included in metadata");

        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // ignore
        }

        throw new RuntimeException("Unhandled exception from Async method");
    }

    @Async
    public void notifyAsync() {
        // Add some thread meta data
        Bugsnag.addThreadMetaData("thread", "inAsyncMethod", "meta data from async method");

        bugsnag.notify(new RuntimeException("test from async"));
    }
}
