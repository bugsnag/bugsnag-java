package com.bugsnag;

@Configuration
public class BugSnagDesktopConfiguration {
    @Autowired
    private Bugsnag bugsnag;



    /**
     * Add a callback to assign specified severities for some Spring exceptions.
     */
    @Override
    public void afterPropertiesSet() {
        bugsnag.addCallback(new ExceptionClassCallback());
    }
}

@Configuration
class JavaxMvcConfiguration implements InitializingBean {

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Register an exception resolver to send unhandled reports 
     * to Bugsnag
     * for uncaught exceptions thrown from request handlers.
     */
    @Bean
    BugsnagJavaxMvcExceptionHandler bugsnagHandlerExceptionResolver() {
        return new BugsnagJavaxMvcExceptionHandler(bugsnag);
    }
}
