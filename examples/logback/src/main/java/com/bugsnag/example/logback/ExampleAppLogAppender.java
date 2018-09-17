package com.bugsnag.example.logback;

import com.bugsnag.Bugsnag;
import com.bugsnag.logback.BugsnagAppender;

/**
 * Example implementation of the abstract BugsnagAppender class.
 */
public class ExampleAppLogAppender extends BugsnagAppender {
    @Override
    protected Bugsnag initBugsnag() {
        return new Bugsnag("YOUR-API-KEY");
    }
}
