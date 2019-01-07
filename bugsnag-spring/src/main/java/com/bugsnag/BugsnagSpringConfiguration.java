package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.SpringVersion;

import javax.annotation.PostConstruct;

/**
 * Configuration to integrate Bugsnag with Spring.
 */
@Configuration
@Import({
        SpringBootConfiguration.class,
        MvcConfiguration.class,
        ScheduledTaskConfiguration.class})
public class BugsnagSpringConfiguration {

    @Autowired
    private Bugsnag bugsnag;

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

    @Bean
    ScheduledTaskBeanLocator scheduledTaskBeanLocator() {
        return new ScheduledTaskBeanLocator();
    }

    /**
     * If using Logback, stop any configured appender from creating Bugsnag reports for Spring log
     * messages as they effectively duplicate error reports for unhandled exceptions.
     */
    @PostConstruct
    void excludeLoggers() {
        // Exclude Tomcat logger when processing HTTP requests via a servlet.
        // Regex specified to match the servlet variable parts of the logger name, e.g.
        // the Spring Boot default is:
        // [Tomcat].[localhost].[/].[dispatcherServlet]
        // but could be something like:
        // [Tomcat-1].[127.0.0.1].[/subdomain/].[customDispatcher]
        BugsnagAppender.addExcludedLoggerPattern("org.apache.catalina.core.ContainerBase."
                + "\\[Tomcat.*\\][.]\\[.*\\][.]\\[/.*\\][.]\\[.*\\]");

        // Exclude Jetty logger when processing HTTP requests via the HttpChannel
        BugsnagAppender.addExcludedLoggerPattern("org.eclipse.jetty.server.HttpChannel");

        // Exclude Undertow logger when processing HTTP requests
        BugsnagAppender.addExcludedLoggerPattern("io.undertow.request");
    }

}
