package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@RestController
public class TestRestController {

    private static final Logger LOGGER = Logger.getLogger(TestRestController.class);

    @Autowired
    Bugsnag bugsnag;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

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
    public String sendUnhandledException() throws InterruptedException {
        throw new RuntimeException("Unhandled exception from TestRestController");
    }

    @RequestMapping("/add-session")
    public String addSession() throws InterruptedException {
        // A session should be automatically recorded by Bugsnag if automatic sessions are enabled
        LOGGER.info("Starting a new session");
        return "";
    }

    @RequestMapping("/run-async-task")
    public String runAsyncTask() throws InterruptedException {
        try {
            asyncMethodService.doSomethingAsync();
        } catch (Exception ex) {
            // This should not happen
            LOGGER.info("Saw exception from async call");
        }

        return "";
    }

}
