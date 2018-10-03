package com.bugsnag.mazerunnerspring;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class Config {

    @Bean
    public Bugsnag bugsnag() {

        // TODO: work out how to inject these into the app

        String path = "http://localhost:9339";

        Bugsnag bugsnag = Bugsnag.init("a35a2a72bd230ac0aa0f52715bbdc6aa");
        bugsnag.setEndpoints(path, path);

        return bugsnag;
    }
}
