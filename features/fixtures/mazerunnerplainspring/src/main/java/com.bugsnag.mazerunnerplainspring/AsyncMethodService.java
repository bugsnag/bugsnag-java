package com.bugsnag.mazerunnerplainspring;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AsyncMethodService {

    @Async
    public void doSomethingAsync() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        throw new RuntimeException("Unhandled exception from Async method");
    }
}
