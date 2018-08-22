package com.bugsnag.example.spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {

    @Autowired
    private String exampleWebsiteLinks;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        return exampleWebsiteLinks + "<br/>Sent a Spring unhandled exception to Bugsnag";
    }
}
