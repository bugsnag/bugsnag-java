package com.bugsnag.testapp.springboot;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
    @Async
    public void throwException() {
        throw new RuntimeException("Async test");
    }
}
