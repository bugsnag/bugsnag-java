package com.bugsnag.servlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class BugsnagServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> cls, ServletContext context) throws ServletException {
        context.addListener(BugsnagServletRequestListener.class);
    }
}
