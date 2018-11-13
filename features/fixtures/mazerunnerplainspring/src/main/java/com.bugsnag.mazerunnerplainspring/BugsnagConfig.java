package com.bugsnag.mazerunnerplainspring;

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
public class BugsnagConfig {

    @Value("${BUGSNAG_API_KEY}")
    private String bugsnagApiKey;

    @Value("${MOCK_API_PATH}")
    private String bugsnagEndpoint;

    @Value("${AUTO_CAPTURE_SESSIONS:false}")
    private boolean autoCaptureSessions;

    @Bean
    public Bugsnag bugsnag() {
        Bugsnag bugsnag = new Bugsnag(bugsnagApiKey);
        bugsnag.setEndpoints(bugsnagEndpoint, bugsnagEndpoint);
        bugsnag.setAutoCaptureSessions(autoCaptureSessions);
        bugsnag.setReleaseStage("production");
        bugsnag.setAppVersion("1.0.0");
        return bugsnag;
    }
}
