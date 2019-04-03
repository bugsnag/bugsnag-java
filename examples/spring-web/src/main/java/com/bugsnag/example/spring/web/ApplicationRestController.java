package com.bugsnag.example.spring.web;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class ApplicationRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRestController.class);

    // Inject the bugsnag notifier bean defined in Config.java
    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private String exampleWebsiteLinks;

    @Autowired
    private AsyncService asyncService;

    @RequestMapping("/")
    public String index() {
        return exampleWebsiteLinks;
    }

    @RequestMapping("/send-handled-exception")
    public String sendHandledException() {
        LOGGER.info("Sending a handled exception to Bugsnag");
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e);
        }

        return exampleWebsiteLinks + "<br/>Sent a handled exception to Bugsnag";
    }

    @RequestMapping("/send-handled-exception-info")
    public String sendHandledExceptionInfo() {
        LOGGER.info("Sending a handled exception to Bugsnag with INFO severity");
        try {
            throw new RuntimeException("Handled exception - INFO severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e, Severity.INFO);
        }

        return exampleWebsiteLinks + "<br/>Sent a handled exception to Bugsnag with INFO severity";
    }

    @RequestMapping("/send-handled-exception-with-metadata")
    public String sendHandledExceptionWithMetadata() {
        LOGGER.info("Sending a handled exception to Bugsnag with custom MetaData");
        try {
            throw new RuntimeException("Handled exception - custom metadata");
        } catch (RuntimeException e) {
            bugsnag.notify(e, new Callback() {
                @Override
                public void beforeNotify(Report report) {
                    report.setSeverity(Severity.WARNING);
                    report.addToTab("report", "something", "that happened");
                    report.setContext("the context");
                }
            });
        }

        return exampleWebsiteLinks + "<br/>Sent a handled exception to Bugsnag with custom MetaData";
    }

    @RequestMapping("/send-unhandled-exception")
    public String sendUnhandledException() {
        LOGGER.info("Sending an unhandled exception to Bugsnag");
        throw new RuntimeException("Sent an unhandled exception to Bugsnag");
    }

    @RequestMapping("/send-unhandled-exception-async")
    public String sendUnhandledExceptionAsync() {
        asyncService.throwExceptionAsync();
        return exampleWebsiteLinks + "<br/>Sent an unhandled exception from an async method";
    }

    @RequestMapping("/send-unhandled-exception-async-future")
    public String sendUnhandledExceptionAsyncFuture() throws ExecutionException, InterruptedException {
        Future future = asyncService.throwExceptionAsyncFuture();
        future.get();

        return "Exception is thrown before this";
    }

    @RequestMapping("/shutdown")
    public void shutdown() {
        LOGGER.info("Shutting down application");

        System.exit(SpringApplication.exit(applicationContext));
    }
}
