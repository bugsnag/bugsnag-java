package com.bugsnag;

import com.bugsnag.servlet.BugsnagServletRequestListener;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class SessionInterceptor extends HandlerInterceptorAdapter {

    private final Bugsnag bugsnag;

    SessionInterceptor(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if (BugsnagServletRequestListener.getServletRequest() == null
                && request.getDispatcherType() != DispatcherType.ERROR
                && bugsnag.shouldAutoCaptureSessions()) {
            bugsnag.startSession();
        }

        return true;
    }
}
