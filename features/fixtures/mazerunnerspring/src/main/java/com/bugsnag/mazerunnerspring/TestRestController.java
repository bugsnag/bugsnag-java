package com.bugsnag.mazerunnerspring;

import com.bugsnag.Bugsnag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

    @Autowired
    Bugsnag bugsnag;


    @RequestMapping("/send-unhandled-exception")
    public String sendUnhandledException() throws InterruptedException {
        throw new RuntimeException("Unhandled exception from TestRestController");
    }

}
