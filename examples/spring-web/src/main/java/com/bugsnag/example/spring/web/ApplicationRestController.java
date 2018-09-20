package com.bugsnag.example.spring.web;

import com.bugsnag.Bugsnag;
import com.bugsnag.Severity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationRestController {

    private static final Logger LOGGER = Logger.getLogger(ApplicationRestController.class);

    // Inject the bugsnag notifier bean defined in Config.java
    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private String exampleWebsiteLinks;

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
            bugsnag.notify(e, (report) -> {
                report.setSeverity(Severity.WARNING);
                report.addToTab("report", "something", "that happened");
                report.setContext("the context");
            });
        }

        return exampleWebsiteLinks + "<br/>Sent a handled exception to Bugsnag with custom MetaData";
    }

    @RequestMapping("/send-unhandled-exception")
    public String sendUnhandledException() throws InterruptedException {
        LOGGER.info("Sending an unhandled exception to Bugsnag");
        throw new RuntimeException("Unhandled exception");
    }

    @RequestMapping("/shutdown")
    public void shutdown() {
        LOGGER.info("Shutting down application");

        SpringApplication.exit(applicationContext);
    }
}
