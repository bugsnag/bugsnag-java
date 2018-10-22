package com.bugsnag.mazerunnerspringboot;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncMethodService {

    @Async
    public void doSomethingAsync() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // ignore
        }

        throw new RuntimeException("Unhandled exception from Async method");
    }
}
