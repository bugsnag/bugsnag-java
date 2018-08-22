package com.bugsnag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;

/**
 * Configuration to integrate Bugsnag with Spring.
 */
@Configuration
public class SpringConfiguration {

    /**
     * If spring-webmvc is loaded, add configuration for reporting unhandled exceptions
     * and session tracking.
     */
    @Configuration
    @Conditional(SpringWebMvcLoadedCondition.class)
    public class SpringWebMvcConfiguration extends WebMvcConfigurerAdapter {

        private final Bugsnag bugsnag;

        public SpringWebMvcConfiguration(final Bugsnag bugsnag) {
            this.bugsnag = bugsnag;
        }

        /**
         * Register an exception resolver to send unhandled reports to Bugsnag
         * for uncaught exceptions thrown from request handlers.
         */
        @Bean
        public BugsnagHandlerExceptionResolver bugsnagHandlerExceptionResolver() {
            return new BugsnagHandlerExceptionResolver(bugsnag);
        }

        /**
         * Add interceptors to automatically start sessions per request
         * and add request metadata to reports.
         */
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            RequestMetadataInterceptor requestMetadataInterceptor =
                    new RequestMetadataInterceptor();
            bugsnag.addCallback(requestMetadataInterceptor);

            SessionInterceptor sessionInterceptor = new SessionInterceptor(bugsnag);

            registry.addInterceptor(requestMetadataInterceptor);
            registry.addInterceptor(sessionInterceptor);
        }

        /**
         * Add a callback to assign specified severities for some Spring exceptions.
         */
        @PostConstruct
        void addErrorClassCallback() {
            bugsnag.addCallback(new ErrorClassCallback());
        }
    }
}
