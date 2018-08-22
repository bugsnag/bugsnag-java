package com.bugsnag;

import com.bugsnag.HandledState.SeverityReasonType;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Reports uncaught exceptions thrown from handler mapping or execution to Bugsnag.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
class BugsnagHandlerExceptionResolver implements HandlerExceptionResolver {

    private final Bugsnag bugsnag;

    BugsnagHandlerExceptionResolver(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         java.lang.Exception ex) {

        if (bugsnag.getConfig().shouldSendUncaughtExceptions()) {
            HandledState handledState = HandledState.newInstance(
                    SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE,
                    Collections.singletonMap("framework", "Spring"),
                    Severity.ERROR,
                    true);

            bugsnag.notify(ex, handledState);
        }

        // Returning null passes the exception onto the next resolver in the chain.
        return null;
    }
}