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
     * Spring Boot requires manual registration of the {@link BugsnagServletRequestListener} using a Spring Boot
     * {@link ServletListenerRegistrationBean}. This adds session tracking and
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
