package com.bugsnag.mazerunner;

import com.bugsnag.Bugsnag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class Scenario {

    @Autowired
    protected Bugsnag bugsnag;

    public abstract void run();

    /**
     * Returns a throwable with the message as the current classname
     */
    protected Throwable generateException(){
        return new RuntimeException(getClass().getName());
    }
}
