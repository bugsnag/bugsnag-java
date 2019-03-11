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
 * Reports uncaught exceptions thrown from handler mapping or execution to Bugsnag
 * and then passes the exception to the next handler in the chain.
 *
 * Set to highest precedence so that it should be called before other exception
 * resolvers.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
class BugsnagMvcExceptionHandler implements HandlerExceptionResolver {

    private final Bugsnag bugsnag;

    BugsnagMvcExceptionHandler(final Bugsnag bugsnag) {
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

            bugsnag.notify(ex, handledState, Thread.currentThread());
        }

        // Returning null passes the exception onto the next resolver in the chain.
        return null;
    }
}
