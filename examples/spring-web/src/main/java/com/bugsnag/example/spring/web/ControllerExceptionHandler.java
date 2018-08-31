package com.bugsnag.example.spring.web;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// We can add an exception handler to a single controller or do it using a @ControllerAdvice across many controllers.
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ControllerExceptionHandler.class);

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private String exampleWebsiteLinks;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        LOGGER.info("Handling exception with Spring ExceptionHandler");
        bugsnag.notify(e, new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setSeverity(Severity.ERROR);
            }
        });
        return exampleWebsiteLinks + "<br/>Sent a Spring handled exception to Bugsnag";
    }
}
