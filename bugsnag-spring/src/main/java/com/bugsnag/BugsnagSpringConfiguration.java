package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import com.bugsnag.servlet.BugsnagServletContainerInitializer;
import com.bugsnag.servlet.BugsnagServletRequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

import java.lang.reflect.Field;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequestListener;

/**
 * Configuration to integrate Bugsnag with Spring.
 */
@Configuration
public class BugsnagSpringConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BugsnagSpringConfiguration.class);

    private final Bugsnag bugsnag;

    BugsnagSpringConfiguration(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    /**
     * Add a callback to add the version of Spring used by the application
     */
    @Bean
    Callback springVersionCallback() {
        Callback callback = new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("device", "springVersion", SpringVersion.getVersion());
            }
        };
        bugsnag.addCallback(callback);
        return callback;
    }

    /**
     * If using Logback, stop any configured appender from creating Bugsnag reports for Spring log
     * messages as they effectively duplicate error reports for unhandled exceptions.
     */
    @PostConstruct
    void excludeLoggers() {

        // Don't try to do anything if Logback is not being used by the application
        if (!bugsnag.isLogbackAppenderInUse()) {
            return;
        }

        // Exclude Tomcat logger when processing HTTP requests via a servlet.
        // Regex specified to match the servlet variable parts of the logger name, e.g.
        // the Spring Boot default is:
        // [Tomcat].[localhost].[/].[dispatcherServlet]
        // but could be something like:
        // [Tomcat-1].[127.0.0.1].[/subdomain/].[customDispatcher]
        BugsnagAppender.setExcludeLoggerRegex("org.apache.catalina.core.ContainerBase."
                        + "\\[Tomcat.*\\][.]\\[.*\\][.]\\[/.*\\][.]\\[.*\\]");

        // Exclude Jetty logger when processing HTTP requests via the HttpChannel
        BugsnagAppender.setExcludeLoggerRegex("org.eclipse.jetty.server.HttpChannel");

        // Exclude Undertow logger when processing HTTP requests
        BugsnagAppender.setExcludeLoggerRegex("io.undertow.request");
    }

    /**
     * If spring-boot is loaded, add configuration specific to Spring Boot
     */
    @Configuration
    @Conditional(SpringBootLoadedCondition.class)
    class SpringBootConfiguration {

        /**
         * Add a callback to add the version of Spring Boot used by the application.
         */
        @Bean
        Callback springBootVersionCallback() {
            Callback callback = new Callback() {
                @Override
                public void beforeNotify(Report report) {
                    report.addToTab("device", "springBootVersion", SpringBootVersion.getVersion());
                }
            };
            bugsnag.addCallback(callback);
            return callback;
        }

        /**
         * The {@link BugsnagServletContainerInitializer} does not work for Spring Boot, need to
         * register the {@link BugsnagServletRequestListener} using a Spring Boot
         * {@link ServletListenerRegistrationBean} instead. This adds session tracking and
         * automatic servlet request metadata collection.
         */
        @Bean
        @Conditional(SpringWebMvcLoadedCondition.class)
        ServletListenerRegistrationBean<ServletRequestListener> listenerRegistrationBean() {
            ServletListenerRegistrationBean<ServletRequestListener> srb =
                    new ServletListenerRegistrationBean<ServletRequestListener>();
            srb.setListener(new BugsnagServletRequestListener());
            return srb;
        }
    }

    /**
     * If spring-webmvc is loaded, add configuration for reporting unhandled exceptions.
     */
    @Configuration
    @Conditional(SpringWebMvcLoadedCondition.class)
    class SpringWebMvcConfiguration {

        /**
         * Register an exception resolver to send unhandled reports to Bugsnag
         * for uncaught exceptions thrown from request handlers.
         */
        @Bean
        BugsnagMvcExceptionHandler bugsnagHandlerExceptionResolver() {
            return new BugsnagMvcExceptionHandler(bugsnag);
        }

        /**
         * Add a callback to assign specified severities for some Spring exceptions.
         */
        @PostConstruct
        void addExceptionClassCallback() {
            bugsnag.addCallback(new ExceptionClassCallback());
        }
    }

    /**
     * Add configuration for reporting unhandled exceptions for scheduled tasks.
     */
    @Configuration
    class SchedulingTaskConfiguration implements SchedulingConfigurer {

        private final BugsnagScheduledTaskExceptionHandler bugsnagErrorHandler =
                new BugsnagScheduledTaskExceptionHandler(bugsnag);

        /**
         * Add bugsnag error handling to a task scheduler
         */
        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
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
}
