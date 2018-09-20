package com.bugsnag.example.spring.web;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Date;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class Config {

    // Define singleton bean "bugsnag" which can be injected into any Spring managed class with @Autowired.
    @Bean
    public Bugsnag bugsnag() {
        // Create a Bugsnag client
        Bugsnag bugsnag = Bugsnag.init("YOUR-API-KEY");

        // Set some diagnostic data which will not change during the
        // lifecycle of the application
        bugsnag.setReleaseStage("staging");
        bugsnag.setAppVersion("1.0.1");

        // Create and attach a simple Bugsnag callback.
        // Use Callbacks to send custom diagnostic data which changes during
        // the lifecyle of your application
        bugsnag.addCallback((report) -> {
            report.addToTab("diagnostics", "timestamp", new Date());
            report.addToTab("customer", "name", "acme-inc");
            report.addToTab("customer", "paying", true);
            report.addToTab("customer", "spent", 1234);
            report.setUserName("User Name");
            report.setUserEmail("user@example.com");
            report.setUserId("12345");
        });

        return bugsnag;
    }

    // The entire code for the example website
    @Bean
    public String exampleWebsiteLinks() {
        return "<a href=\"/send-handled-exception\">Send a handled exception to Bugsnag</a><br/>"
                + "<a href=\"/send-handled-exception-info\">Send a handled exception to Bugsnag with INFO severity</a><br/>"
                + "<a href=\"/send-handled-exception-with-metadata\">Send a handled exception to Bugsnag with custom MetaData</a><br/>"
                + "<a href=\"/send-unhandled-exception\">Send an unhandled exception to Bugsnag</a><br/>"
                + "<a href=\"/shutdown\">Shutdown the application</a><br/>";
    }
}
