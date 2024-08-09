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