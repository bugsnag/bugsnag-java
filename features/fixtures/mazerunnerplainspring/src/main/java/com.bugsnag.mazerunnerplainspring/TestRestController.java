package com.bugsnag.mazerunnerplainspring;

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

    @RequestMapping("/send-unhandled-exception")
    public String sendUnhandledException() throws InterruptedException {
        throw new RuntimeException("Unhandled exception from TestRestController");
    }
}
