package com.bugsnag.mazerunnerspringboot;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class Config {

    @Value("${bugsnag.api_key}")
    private String bugsnagApiKey;

    @Value("${bugsnag.endpoint}")
    private String bugsnagEndpoint;

    @Bean
    public Bugsnag bugsnag() {
        Bugsnag bugsnag = Bugsnag.init(bugsnagApiKey);
        bugsnag.setEndpoints(bugsnagEndpoint, bugsnagEndpoint);
        bugsnag.setReleaseStage("production");
        bugsnag.setAppVersion("1.0.0");
        return bugsnag;
    }
}
