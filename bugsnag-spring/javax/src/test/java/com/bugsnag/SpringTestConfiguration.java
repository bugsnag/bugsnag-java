package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.SpringVersion;

import java.util.Map;

/**
 * Configuration to test Bugsnag with Spring v5.
 */
@Configuration
@Import({
        SpringBootV2Configuration.class,
        JavaxMvcConfiguration.class,
        ScheduledTaskConfiguration.class})
public class SpringTestConfiguration implements InitializingBean {

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Add a callback to add the version of Spring used by the application
     */
    @Bean
    Callback springVersionErrorCallback() {
        Callback callback = new Callback() {
            @Override
            public void beforeNotify(Report report) {
                addSpringRuntimeVersion(report.getDevice());
            }
        };
        bugsnag.addCallback(callback);
        return callback;
    }

    @Bean
    BeforeSendSession springVersionSessionCallback() {
        BeforeSendSession beforeSendSession = new BeforeSendSession() {
            @Override
            public void beforeSendSession(SessionPayload payload) {
                addSpringRuntimeVersion(payload.getDevice());
            }
        };
        bugsnag.addBeforeSendSession(beforeSendSession);
        return beforeSendSession;
    }

    private void addSpringRuntimeVersion(Map<String, Object> device) {
        Diagnostics.addDeviceRuntimeVersion(device, "springFramework", SpringVersion.getVersion());
    }

    @Bean
    ScheduledTaskBeanLocator scheduledTaskBeanLocator() {
        return new ScheduledTaskBeanLocator();
    }

    @Override
    public void afterPropertiesSet() {
        try {
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
        } catch (NoClassDefFoundError ignored) {
            // logback was not in classpath, ignore throwable to allow further initialisation
        }
    }
}
