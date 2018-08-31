package com.bugsnag.example.spring.cli;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class Config {

    // Define singleton bean "bugsnag" which can be injected into any Spring managed class with @Autowired.
    @Bean
    public Bugsnag bugsnag() {
        // Create a Bugsnag client
        Bugsnag bugsnag = new Bugsnag("YOUR-API-KEY");

        // Set some diagnostic data which will not change during the
        // lifecycle of the application
        bugsnag.setReleaseStage("staging");
        bugsnag.setAppVersion("1.0.1");

        // Create and attach a simple Bugsnag callback.
        // Use Callbacks to send custom diagnostic data which changes during
        // the lifecyle of your application
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("diagnostics", "timestamp", new Date());
                report.addToTab("customer", "name", "acme-inc");
                report.addToTab("customer", "paying", true);
                report.addToTab("customer", "spent", 1234);
                report.setUserName("User Name");
                report.setUserEmail("user@example.com");
                report.setUserId("12345");
            }
        });

        return bugsnag;
    }
}
