package com.bugsnag;

import com.bugsnag.servlet.BugsnagServletRequestListener;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Automatically start a session for incoming HTTP requests.
 */
class SessionInterceptor extends HandlerInterceptorAdapter {

    private final Bugsnag bugsnag;

    SessionInterceptor(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // If the request is available through the listener then the session has already started.
        // This is only the case for plain Spring apps, ServletRequestListeners do not work for
        // Spring Boot apps.
        if (BugsnagServletRequestListener.getServletRequest() == null
                && request.getDispatcherType() != DispatcherType.ERROR
                && bugsnag.shouldAutoCaptureSessions()) {
            bugsnag.startSession();
        }

        return true;
    }
}
