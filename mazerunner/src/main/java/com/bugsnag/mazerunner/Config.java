package com.bugsnag.mazerunner;

import com.bugsnag.Bugsnag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class Config {
    @Bean
    public Bugsnag bugsnag() {
        return new Bugsnag("YOUR-API-KEY");
    }
}
