package com.bugsnag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class SpringConfiguration {

    @Autowired
    private Bugsnag bugsnag;

    @Bean
    public BugsnagHandlerExceptionResolver bugsnagHandlerExceptionResolver() {
        return new BugsnagHandlerExceptionResolver(bugsnag);
    }

    @Configuration
    public class BugsnagInterceptorConfigurerAdapter extends WebMvcConfigurerAdapter {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            RequestMetadataInterceptor interceptor = new RequestMetadataInterceptor();
            bugsnag.addCallback(interceptor);
            registry.addInterceptor(interceptor);
        }
    }
}
