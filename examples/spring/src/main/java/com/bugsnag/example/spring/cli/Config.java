package com.bugsnag.example.spring.cli;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import com.bugsnag.BugsnagEvent;
import com.bugsnag.callbacks.Callback;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Date;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class Config {

    // Define singleton bean "bugsnag" which can be injected into any Spring managed
    // class with @Autowired.
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
            public boolean onError(BugsnagEvent event) {
                event.addMetadata("diagnostics", "timestamp", new Date());
                event.addMetadata("customer", "name", "acme-inc");
                event.addMetadata("customer", "paying", true);
                event.addMetadata("customer", "spent", 1234);
                event.setUserName("User Name");
                event.setUserEmail("user@example.com");
                event.setUserId("12345");
                return true;
            }
        });

        return bugsnag;
    }
}
