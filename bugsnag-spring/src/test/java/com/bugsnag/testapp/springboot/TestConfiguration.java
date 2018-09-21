package com.bugsnag.testapp.springboot;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagAsyncExceptionHandler;
import com.bugsnag.BugsnagSpringConfiguration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

/**
 * This test configuration loads the BugsnagSpringConfiguration
 * that will be used for real Spring bugsnag integration.
 */
@Configuration
@Import(BugsnagSpringConfiguration.class)
public class TestConfiguration extends AsyncConfigurerSupport implements SchedulingConfigurer {

    @Autowired(required = false)
    private ErrorHandler scheduledTaskErrorHandler;

    @Bean
    public Bugsnag bugsnag() {
        return Bugsnag.init("apiKey");
    }

    @Bean
    ThreadPoolTaskScheduler scheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setErrorHandler(scheduledTaskErrorHandler);
        return taskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduler());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new BugsnagAsyncExceptionHandler(bugsnag());
    }
}
