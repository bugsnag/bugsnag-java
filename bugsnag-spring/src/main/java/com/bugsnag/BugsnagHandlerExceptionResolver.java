package com.bugsnag;

import com.bugsnag.HandledState.SeverityReasonType;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BugsnagHandlerExceptionResolver implements HandlerExceptionResolver {
    private final Bugsnag bugsnag;

    public BugsnagHandlerExceptionResolver(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         java.lang.Exception ex) {
        HandledState handledState = HandledState.newInstance(
                SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE,
                Collections.singletonMap("framework", "Spring"),
                Severity.ERROR,
                true);
        bugsnag.notify(ex, handledState);
        return null;
    }
}