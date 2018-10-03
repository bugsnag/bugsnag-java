package com.bugsnag.mazerunnerspring;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

    private static final Logger LOGGER = Logger.getLogger(TestRestController.class);

    @Autowired
    Bugsnag bugsnag;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @RequestMapping("/send-unhandled-exception")
    public String sendUnhandledException() throws InterruptedException {
        throw new RuntimeException("Unhandled exception from TestRestController");
    }

    @RequestMapping("/run-async-task")
    public String runAsyncTask() throws InterruptedException {
        try {
            scheduledTaskService.doSomethingAsync();
        } catch (Exception ex) {
            // This should not happen
            LOGGER.info("Saw exception from async call");
        }

        return "";
    }

}
