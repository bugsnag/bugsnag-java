package com.bugsnag.testapp.springboot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TestExceptionHandler {
    @ExceptionHandler(TestCustomException.class)
    public ResponseEntity handleTestCustomException(TestCustomException ignored) {
        return ResponseEntity.ok(TestCustomException.class.getSimpleName());
    }
}
