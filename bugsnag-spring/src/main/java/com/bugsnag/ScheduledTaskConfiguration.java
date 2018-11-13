package com.bugsnag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

import java.lang.reflect.Field;

/**
 * Add configuration for reporting unhandled exceptions for scheduled tasks.
 */
@Configuration
class ScheduledTaskConfiguration implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskConfiguration.class);

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Add bugsnag error handling to a task scheduler
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        BugsnagScheduledTaskExceptionHandler bugsnagErrorHandler =
                new BugsnagScheduledTaskExceptionHandler(bugsnag);

        if (taskRegistrar.getScheduler() == null) {

            // If no task scheduler has been defined by the application, create one
            // and add the bugsnag error handler.
            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setErrorHandler(bugsnagErrorHandler);
            taskScheduler.initialize();
            taskRegistrar.setScheduler(taskScheduler);
        } else {

            // If a task scheduler has been defined by the application, get it so that
            // bugsnag error handling can be added.
            TaskScheduler taskScheduler = taskRegistrar.getScheduler();

            // Reflection is the simplest way to get and set an error handler
            // because the error handler setter is only defined in the concrete classes,
            // not the TaskScheduler interface.
            try {
                Field errorHandlerField =
                        taskScheduler.getClass().getDeclaredField("errorHandler");
                errorHandlerField.setAccessible(true);
                Object existingErrorHandler = errorHandlerField.get(taskScheduler);

                // If an error handler has already been defined then make the Bugsnag handler
                // call this afterwards
                if (existingErrorHandler instanceof ErrorHandler) {
                    bugsnagErrorHandler.setExistingErrorHandler(
                            (ErrorHandler) existingErrorHandler);
                }

                // Add the bugsnag error handler to the scheduler.
                errorHandlerField.set(taskScheduler, bugsnagErrorHandler);
            } catch (NoSuchFieldException ex) {
                logScheduledErrorHandlerNotConfigured();
            } catch (IllegalArgumentException ex) {
                logScheduledErrorHandlerNotConfigured();
            } catch (IllegalAccessException ex) {
                logScheduledErrorHandlerNotConfigured();
            } catch (SecurityException ex) {
                logScheduledErrorHandlerNotConfigured();
            }
        }
    }

    private void logScheduledErrorHandlerNotConfigured() {
        LOGGER.warn("Bugsnag scheduled task exception handler could not be configured");
    }
}