package com.bugsnag;

import com.bugsnag.servlet.jakarta.BugsnagServletRequestListener;

import jakarta.servlet.ServletRequestListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * If spring-boot is loaded, add configuration specific to Spring Boot
 */
@Configuration
@Conditional(SpringBootLoadedCondition.class)
class SpringBootJakartaConfiguration extends SpringBootConfiguration {

    /**
     * The {@link com.bugsnag.servlet.javax.BugsnagServletContainerInitializer} does not work for Spring Boot, need to
     * register the {@link BugsnagServletRequestListener} using a Spring Boot
     * {@link ServletListenerRegistrationBean} instead. This adds session tracking and
     * automatic servlet request metadata collection.
     */
    @Bean
    @Conditional(SpringWebJakartaMvcLoadedCondition.class)
    ServletListenerRegistrationBean<ServletRequestListener> listenerRegistrationBean() {
        ServletListenerRegistrationBean<ServletRequestListener> srb =
                new ServletListenerRegistrationBean<ServletRequestListener>();
        srb.setListener(new BugsnagServletRequestListener());
        return srb;
    }
}
