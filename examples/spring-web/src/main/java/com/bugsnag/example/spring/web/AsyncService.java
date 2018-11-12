package com.bugsnag.example.spring.web;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
class AsyncService {

    @Async
    void throwExceptionAsync() {
        throw new RuntimeException("Sent unhandled exception from async method");
    }

    @Async
    Future throwExceptionAsyncFuture() {
        throw new RuntimeException("Sent an unhandled exception from an async method that returns a Future");
    }

}
