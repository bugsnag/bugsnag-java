package com.bugsnag.testapp.springboot;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AsyncService {
    @Async
    public void throwExceptionVoid() {
        throw new RuntimeException("Async void test");
    }

    @Async
    public Future throwExceptionFuture() {
        throw new RuntimeException("Async future test");
    }
}
