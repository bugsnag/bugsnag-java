package com.bugsnag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;

@Configuration
public class SpringConfiguration {

    private final Bugsnag bugsnag;

    public SpringConfiguration(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Bean
    public BugsnagHandlerExceptionResolver bugsnagHandlerExceptionResolver() {
        return new BugsnagHandlerExceptionResolver(bugsnag);
    }

    @Configuration
    public class BugsnagInterceptorConfigurerAdapter extends WebMvcConfigurerAdapter {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            RequestMetadataInterceptor requestMetadataInterceptor =
                    new RequestMetadataInterceptor();
            bugsnag.addCallback(requestMetadataInterceptor);

            SessionInterceptor sessionInterceptor = new SessionInterceptor(bugsnag);

            registry.addInterceptor(requestMetadataInterceptor);
            registry.addInterceptor(sessionInterceptor);
        }
    }

    @PostConstruct
    void addExceptionClassCallback() {
        bugsnag.addCallback(new ExceptionClassCallback());
    }
}
