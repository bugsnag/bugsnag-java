package com.bugsnag.servlet;

import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class BugsnagServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> cls, ServletContext context) throws ServletException {
        context.addListener(BugsnagServletRequestListener.class);
    }
}
