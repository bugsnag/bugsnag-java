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

    private final String links =
            "<a href=\"/send-handled-exception\">Send a handled exception to Bugsnag</a><br/>"
            + "<a href=\"/send-handled-exception-info\">Send a handled exception to Bugsnag with INFO severity</a><br/>"
            + "<a href=\"/send-handled-exception-with-metadata\">Send a handled exception to Bugsnag with custom MetaData</a><br/>"
            + "<a href=\"/send-unhandled-exception\">Send an unhandled exception to Bugsnag</a><br/>"
            + "<a href=\"/shutdown\">Shutdown the application</a><br/>";

    @RequestMapping("/")
    public String index() {
        return links;
    }

    @RequestMapping("/send-handled-exception")
    public String sendHandledException() {
        LOGGER.info("Sending a handled exception to Bugsnag");
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e);
        }

        return links + "<br/>Sent a handled exception to Bugsnag";
    }

    @RequestMapping("/send-handled-exception-info")
    public String sendHandledExceptionInfo() {
        LOGGER.info("Sending a handled exception to Bugsnag with INFO severity");
        try {
            throw new RuntimeException("Handled exception - INFO severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e, Severity.INFO);
        }

        return links + "<br/>Sent a handled exception to Bugsnag with INFO severity";
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

        return links + "<br/>Sent a handled exception to Bugsnag with custom MetaData";
    }

    @RequestMapping("/send-unhandled-exception")
    public String sendUnhandledException() throws InterruptedException {
        // Test an unhanded exception from a different thread as shutdown hooks
        // won't be called if executed from this thread
        LOGGER.info("Sending an unhandled exception to Bugsnag");
        Thread thread = new Thread() {
            @Override
            public void run() {
                throw new RuntimeException("Unhandled exception");
            }
        };

        thread.start();

        // Wait for unhandled exception thread to finish
        thread.join();

        return links + "<br/>Sent an unhandled exception to Bugsnag";
    }

    @RequestMapping("/shutdown")
    public void shutdown() throws InterruptedException {
        LOGGER.info("Shutting down application");

        SpringApplication.exit(applicationContext);
    }
}
