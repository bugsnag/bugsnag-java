package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRestController.class);

    @Autowired
    Bugsnag bugsnag;

    @Autowired
    private AsyncMethodService asyncMethodService;

    private static TestRestController instance;

    public TestRestController() {
        instance = this;
    }

    public static Bugsnag getBugsnag() {
        return instance.bugsnag;
    }

    @RequestMapping("/send-unhandled-exception")
    public String sendUnhandledException() {
        throw new RuntimeException("Unhandled exception from TestRestController");
    }

    @RequestMapping("/add-session")
    public String addSession() {
        // A session should be automatically recorded by Bugsnag if automatic sessions are enabled
        LOGGER.info("Starting a new session");
        return "";
    }

    @RequestMapping("/run-async-task")
    public String runAsyncTask() {
        try {
            asyncMethodService.doSomethingAsync();
        } catch (Exception ex) {
            // This should not happen
            LOGGER.info("Saw exception from async call");
        }

        return "";
    }

    @RequestMapping("/notify-async-task")
    public String notifyAsyncTask() {

        // Add some thread meta data
        Bugsnag.addThreadMetaData("thread", "controllerMethod", "meta data from controller method");

        // Notify before calling the async method
        bugsnag.notify(new RuntimeException("test from before async"));

        // Call the async method (also notifies)
        asyncMethodService.notifyAsync();

        return "";
    }

}
